//
//  ScenicVista.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ScenicVista.h"

@implementation ScenicVista

@synthesize actionId, actionType, prompt, date, lat, lng, note, photoUrl;
@synthesize location, complete;

+ (ScenicVista *) initWithDictionary:(NSDictionary *)dict
{
    ScenicVista *vista = [[ScenicVista alloc] init];
    [vista setActionId:[dict objectForKey:@"action_id"]];
    [vista setActionType:[dict objectForKey:@"action_type"]];
    [vista setPrompt:[dict objectForKey:@"verbiage"]];
    [vista setDate:[dict objectForKey:@"date"]];
    [vista setLat:[dict objectForKey:@"latitude"]];
    [vista setLng:[dict objectForKey:@"longitude"]];
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



-(BOOL) isEqual:(id)object
{
    NSLog(@"Calling my vista is equal function: %@", ([location isEqual:[object location]] ? @"YES" : @"NO"));
    if (![object isKindOfClass:[ScenicVista class]])
        return NO;
    return [location isEqual:[object location]];
        
}

- (NSUInteger)hash
{   NSLog(@"Calling my vista hash function, %f", (location.coordinate.latitude + location.coordinate.longitude));
    return (location.coordinate.latitude + location.coordinate.longitude);
}
            
@end
