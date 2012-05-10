//
//  ScenicVista.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ScenicVista.h"

@implementation ScenicVista

@synthesize actionId, actionType, prompt, date, note, photoUrl, photoLocalUrl;
@synthesize location, complete, region;

+ (ScenicVista *) initWithDictionary:(NSDictionary *)dict
{
    ScenicVista *vista = [[ScenicVista alloc] init];
    if ([dict objectForKey:@"new_vista_action"] != nil){
        NSDictionary *newAction = [dict objectForKey:@"new_vista_action"];
        [vista setActionId:[newAction objectForKey:@"action_id"]];
        [vista setActionType:[newAction objectForKey:@"action_type"]];
        [vista setPrompt:[newAction objectForKey:@"verbiage"]];
    }
    else{
        [vista setActionId:[dict objectForKey:@"action_id"]];
        [vista setActionType:[dict objectForKey:@"action_type"]];
        [vista setPrompt:[dict objectForKey:@"verbiage"]];
    }
    [vista setDate:[dict objectForKey:@"date"]];
    [vista setLocation:[[CLLocation alloc] initWithLatitude:[[dict objectForKey:@"latitude"] doubleValue] longitude:[[dict objectForKey:@"longitude"] doubleValue]]];
    [vista setNote:[dict objectForKey:@"note"]];
    [vista setPhotoUrl:[dict objectForKey:@"photo"]];
    return vista;
}

+ (ScenicVista *) initWithPoint:(CLLocation *)point
{
    ScenicVista *vista = [[ScenicVista alloc] init];
    [vista setLocation:point];
    return vista;
}

- (ActionType) getActionType{
    if ([actionType isEqualToString:@"photo"])
       return PHOTO;
    else if ([actionType isEqualToString:@"note"])
        return NOTE;
    else if ([actionType isEqualToString:@"meditate"])
        return MEDITATE;
    return TEXT;
}

- (NSString *) toJson
{
//    NSMutableString *str = [[NSMutableString alloc] init];
//    [str appendFormat:@"latitude=%f", location.coordinate.latitude];
//	[str appendFormat:@"longitude=%f", location.coordinate.longitude];
//	[str appendFormat:@"action_id=%@", actionId];
//	[str appendFormat:@"note=%@", note];
//    if ([self getActionType] == PHOTO){
//        photoUrl = [self getUploadFileName];
//        [str appendFormat:@"photo=%@", photoUrl];
//    }
//	[str appendFormat:@"date=%@", date];
//    return str;
     NSMutableString *str = [NSMutableString stringWithFormat:@"{\"latitude\": %f, \"longitude\": %f, \"action_id\": %@, \"note\": \"%@\",\"photo\":\"%@\", \"date\":\"%@\"}", 
                              location.coordinate.latitude,
                              location.coordinate.longitude,
                              actionId,
                              note,
                              [self getUploadFileName],
                              date];
    NSLog(@"vista json: %@", str);
    return str;
}

- (NSData *) getUploadPhoto
{
    if (photoLocalUrl != nil){
        return [NSData dataWithContentsOfURL:photoLocalUrl];
    }
    return nil;
}

-(NSString *) getUploadFileName
{
      return [NSString stringWithFormat:@"IH_%i,%i", location.coordinate.latitude*1000000, location.coordinate.longitude *1000000];
}

-(BOOL) isEqual:(id)object
{
    if (![object isKindOfClass:[ScenicVista class]])
        return NO;
    if (location.coordinate.latitude != [(ScenicVista *) object location].coordinate.latitude)
        return NO;
    else{
        if (location.coordinate.longitude != [(ScenicVista *) object location].coordinate.longitude)
            return NO;
        return YES;
    }
   // return [location isEqual:[object location]];
        
}

- (NSUInteger)hash
{
    return (location.coordinate.latitude + location.coordinate.longitude);
}
            
@end
