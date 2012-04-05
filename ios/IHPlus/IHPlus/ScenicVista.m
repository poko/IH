//
//  ScenicVista.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ScenicVista.h"

typedef enum {
    NOTE,
    PHOTO,
    TEXT,
    MEDITATE
} ActionType;


@implementation ScenicVista

@synthesize actionId, date, lat, lng, note, photoUrl;

+ (ScenicVista *) initWithDictionary:(NSDictionary *)dict
{
    ScenicVista *vista = [[ScenicVista alloc] init];
    [vista setActionId:[dict objectForKey:@"action_id"]];
    [vista setDate:[dict objectForKey:@"date"]];
    [vista setLat:[dict objectForKey:@"latitude"]];
    [vista setLng:[dict objectForKey:@"longitude"]];
    [vista setNote:[dict objectForKey:@"note"]];
    [vista setPhotoUrl:[dict objectForKey:@"photo"]];
    return vista;
}

@end
