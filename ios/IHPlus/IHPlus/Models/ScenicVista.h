//
//  ScenicVista.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

typedef enum {
    NOTE,
    PHOTO,
    TEXT,
    MEDITATE
} ActionType;

@interface ScenicVista : NSObject

@property (nonatomic, strong) NSString *actionId;
@property (nonatomic, strong) NSString *actionType;
@property (nonatomic, strong) NSString *prompt;
@property (nonatomic, strong) NSString *date;
@property (nonatomic, strong) NSString *note;
@property (nonatomic, strong) NSString *photoUrl;
@property (nonatomic, strong) CLLocation *location;
@property (nonatomic) BOOL complete;
@property (nonatomic, strong) CLRegion *region;

+ (ScenicVista *) initWithDictionary:(NSDictionary *) dict;
+ (ScenicVista *) initWithPoint:(CLLocation *) point;
- (ActionType) getActionType;
- (NSString *) toJson;

@end
