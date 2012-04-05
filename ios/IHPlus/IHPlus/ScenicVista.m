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

- (ActionType) getActionType{
    if ([actionType isEqualToString:@"photo"])
       return PHOTO;
    return NOTE;
}

@end
