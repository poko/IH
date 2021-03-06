//
//  Hike.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "Hike.h"

@implementation Hike

@synthesize hikeId, date, name, description, username, companion;
@synthesize vistas, original, originalHikeId, points, startLat, startLng;

+ (Hike *) initWithDictionary:(NSDictionary *)dict
{
    Hike *hike = [[Hike alloc] init];
    [hike setHikeId:[dict objectForKey:@"hike_id"]];
    [hike setName:[dict objectForKey:@"name"]];
    [hike setDescription:[dict objectForKey:@"description"]];
    [hike setUsername:[dict objectForKey:@"username"]];
    // start lat/lng
    [hike setStartLat:[dict objectForKey:@"start_lat"]];
    [hike setStartLng:[dict objectForKey:@"start_lng"]];
    // set date
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat: @"yyy-MM-dd HH:mm:ss"];
    NSDate *d = [dateFormatter dateFromString:[dict objectForKey:@"date"]];
    [hike setDate:d];
    // set original flag & id
    [hike setOriginal:[dict objectForKey:@"orginal"]];
    [hike setOriginalHikeId:[dict objectForKey:@"orginal_hike_id"]];
    // set points
    hike.points = [NSMutableArray array];
    NSArray *pointsJson = [dict objectForKey:@"points"];
    for (NSDictionary *pointJson in pointsJson) {
        float lat = ([[pointJson objectForKey:@"latitude"] floatValue]/1000000);
        float lng = ([[pointJson objectForKey:@"longitude"] floatValue]/1000000);
        CLLocation *point = [[CLLocation alloc] initWithLatitude:lat longitude:lng];
        [hike.points addObject:point];
    }
    // set vistas
    hike.vistas = [NSMutableArray array];
    NSArray *vistasJson = [dict objectForKey:@"vistas"];
    for (NSDictionary *vistaJson in vistasJson) {
        ScenicVista *vista = [ScenicVista initWithDictionary:vistaJson];
        [hike.vistas addObject:vista];
    }
    // set companion
    [hike setCompanion:[[dict objectForKey:@"companion"] isEqual:@"true"]];
    
    return hike;
}

- (void) addPoint:(CLLocation *)point
{
    if (points == nil)
        points = [NSMutableArray array];
    [points addObject:point];
}

- (void) addVista:(CLLocation *) point
{
    if (vistas == nil)
        vistas = [NSMutableArray array];
    ScenicVista *vista = [ScenicVista initWithPoint:point];
    if (![vistas containsObject:vista])
        [vistas addObject:vista];
}

- (void) addCompanionVista:(ScenicVista *)vista
{
    if (vistas == nil)
        vistas = [NSMutableArray array];
    if (![vistas containsObject:vista])
        [vistas addObject:vista];
}

- (ScenicVista *) getVistaById:(NSString *)actionId
{
    for (ScenicVista *vista in vistas){
        if ([[vista actionId] isEqualToString:actionId])
            return vista;
    }
    return nil;
}


- (bool) eligibleForUpload
{
   int completedVistas = 0;
   for (ScenicVista *v in vistas){
        if ([v complete])
            completedVistas++;
    }
    return completedVistas > 2;
}

- (bool) isComplete
{
  if (companion)
        return false; // companion hikes are never 'complete', just eligible for upload.
    for (ScenicVista *vista in vistas){
        if (![vista complete])
            return false;
    }
    return true;
}

- (bool) hasCompletedVista
{
    for (ScenicVista *vista in vistas){
        if ([vista complete])
            return true;
    }
    return false;
}

- (NSString *) vistasAsJson
{
    NSMutableString *str = [NSMutableString stringWithString:@"["];
    for (ScenicVista *vista in vistas){
        [str appendFormat:@"%@,", [vista toJson]];
    }
    // remove last comma
    [str deleteCharactersInRange:NSMakeRange([str length]-1, 1)];
    [str appendString:@"]"];
    //NSLog(@"vistas as json: %@", str);
    return str;
}

- (NSString *) pointsAsJson
{
    NSMutableString *str = [NSMutableString stringWithString:@"["];
    for (int i = 0; i < [points count]; i++) {
        CLLocation *point = [points objectAtIndex:i];
        [str appendFormat:@"{\"index\": %i, \"latitude\": %f, \"longitude\": %f},",
         i, (1000000 * point.coordinate.latitude), (1000000 * point.coordinate.longitude)];
    }
    // remove last comma
    [str deleteCharactersInRange:NSMakeRange([str length]-1, 1)];
    [str appendString:@"]"];
    return str;
}

-(void) appendToData:(NSMutableData *) data key:(NSString *) key value:(NSString *) value
{
    NSString*   entry = [NSString stringWithFormat:
                         @"--%@\r\nContent-Disposition: form-data; name=\"%@\"\r\n\r\n%@\r\n",
                         @"####", key, value];
    [data appendData:[entry dataUsingEncoding:NSUTF8StringEncoding]];
}

- (NSData *) getUploadData
{
    
    NSMutableData*      data = [[NSMutableData alloc] init];
    [self appendToData:data key:@"hike_name" value: name];
    [self appendToData:data key:@"description" value: description];
    [self appendToData:data key:@"username" value: username];
    [self appendToData:data key:@"original" value: original];
    [self appendToData:data key:@"vistas" value: [self vistasAsJson]];
    if ([original isEqualToString:@"true"]){
        [self appendToData:data key:@"start_lat" value: startLat];
        [self appendToData:data key:@"start_lng" value: startLng];
        [self appendToData:data key:@"points" value: [self pointsAsJson]];
    }
    if (companion){
        [self appendToData:data key:@"companion" value:@"true"];
    }

    
    // upload photos
    for (int i = 0; i < [vistas count]; i++){
        ScenicVista *vista = [vistas objectAtIndex:i];
        if ([vista getActionType] == PHOTO) {
            NSString*       head = [NSString stringWithFormat:@"--%@\r\nContent-Disposition: form-data; name=\"photos_%i\"",
                                    @"####", i];
            
            if([vista getUploadFileName]){
                head = [head stringByAppendingFormat:@"; filename=\"%@.jpg\"", [vista getUploadFileName]];
            }
            NSLog(@"trying to upload file with length: %i", [[vista getUploadPhoto] length] );
            head = [head stringByAppendingFormat:@"\r\nContent-Length: %d\r\n\r\n", [[vista getUploadPhoto] length]];
            NSLog(@"head str: %@", head);
            [data appendData:[head dataUsingEncoding:NSUTF8StringEncoding]];
            [data appendData:[vista getUploadPhoto]];
            [data appendData:[[NSString stringWithFormat:@"\r\n--%@--\r\n", @"####"] dataUsingEncoding:NSUTF8StringEncoding]];
        }
    }

    return data;
}

@end
