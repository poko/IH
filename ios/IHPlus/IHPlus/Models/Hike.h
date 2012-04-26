//
//  Hike.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>
#import "ScenicVista.h"

@interface Hike : NSObject

@property (nonatomic, strong) NSString *hikeId;
@property (nonatomic, strong) NSDate *date;
@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSString *description;
@property (nonatomic, strong) NSString *username;
@property (nonatomic, strong) NSMutableArray *vistas; //holds ScenicVista objects
@property (nonatomic, strong) NSString *original;
@property (nonatomic, strong) NSString *originalHikeId;
@property (nonatomic, strong) NSMutableArray *points; //holds CLLocation objects
@property (nonatomic, strong) NSString *startLat;
@property (nonatomic, strong) NSString *startLng;

+ (Hike *) initWithDictionary:(NSDictionary *) dict;
- (void) addPoint:(CLLocation *) point;
- (void) addVista:(CLLocation *) point;
- (void) addCompanionVista:(ScenicVista *) vista;
- (ScenicVista *) getVistaById:(NSString *) actionId;
- (bool) eligibleForUpload;
- (bool) isComplete;
- (NSData *) getUploadData;
- (bool) hasCompletedVista;

@end
