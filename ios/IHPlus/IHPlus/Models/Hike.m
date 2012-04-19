//
//  Hike.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Hike.h"

@implementation Hike

@synthesize hikeId, date, name, description, username;
@synthesize vistas, original, originalHikeId, points, startLat, startLng;

+ (Hike *) initWithDictionary:(NSDictionary *)dict
{
    Hike *hike = [[Hike alloc] init];
    [hike setHikeId:[dict objectForKey:@"hike_id"]];
    [hike setName:[dict objectForKey:@"name"]];
    [hike setDescription:[dict objectForKey:@"description"]];
    [hike setUsername:[dict objectForKey:@"username"]];
    // set date
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat: @"yyy-MM-dd HH:mm:ss"];
    NSDate *d = [dateFormatter dateFromString:[dict objectForKey:@"date"]];
    [hike setDate:d];
    // set original flag & id
    [hike setOriginal:[dict objectForKey:@"orginal"]];
    [hike setOriginalHikeId:[dict objectForKey:@"orginal_hike_id"]];
    // set vistas
    hike.vistas = [NSMutableArray array];
    NSArray *vistasJson = [dict objectForKey:@"vistas"];
    for (NSDictionary *vistaJson in vistasJson) {
        ScenicVista *vista = [ScenicVista initWithDictionary:vistaJson];
        [hike.vistas addObject:vista];
    }
    // set points
    
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

- (ScenicVista *) getVistaById:(NSString *)actionId
{
    for (ScenicVista *vista in vistas){
        if ([[vista actionId] isEqualToString:actionId])
            return vista;
    }
    return nil;
}

bool eligble = false;
- (bool) eligibleForUpload
{
    if (eligble)
        return true;
    else{
        int completedVistas = 0;
        for (ScenicVista *v in vistas){
            if ([v complete])
                completedVistas++;
        }
        eligble = completedVistas > 2;
    }
    return eligble;
}

- (bool) isComplete
{
//     TODO uncomment! for (ScenicVista *vista in vistas){
//        if (![vista complete])
//            return false;
//    }
    return true;
}

- (NSString *) vistasAsJson
{
    NSMutableString *str = [[NSMutableString alloc] init];
    for (ScenicVista *vista in vistas){
        [str appendFormat:@"%@,", [vista toJson]];
    }
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
    NSLog(@"points as json: %@", str);
    return str;
}

- (NSData *) getUploadData
{
    NSMutableString *data = [NSMutableString stringWithFormat:@"hike_name=%@&description=%@&username=%@",
                      name, description, username];
    [data appendFormat:@"&original=%@", original];
    [data appendFormat:@"&vistas=%@", [self vistasAsJson]];
    if ([original isEqualToString:@"true"]){
        [data appendFormat:@"&start_lat=%@", startLat];
        [data appendFormat:@"&start_lng=%@", startLng];
        [data appendFormat:@"&points=%@", [self pointsAsJson]];
    }
//    NSError *error;
//    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
//    [dict setValue:name forKey:@"hike_name"];
//    [dict setValue:description forKey:@"description"];
//    [dict setValue:username forKey:@"username"];
    
    //convert object to data
//    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
//    if (error != nil){
//        NSLog(@"there was an error converting the dictionary?? %@", [error description]);
//    }
    return [data dataUsingEncoding:NSUTF8StringEncoding];
}

@end
