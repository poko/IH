//
//  Hike.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Hike.h"
#import "ScenicVista.h"

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

@end
