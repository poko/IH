//
//  Hike.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Hike : NSObject

@property (nonatomic, strong) NSString *hikeId;
@property (nonatomic, strong) NSDate *date;
@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSString *description;
@property (nonatomic, strong) NSString *username;

+ (Hike *) initWithDictionary:(NSDictionary *) dict;

@end
