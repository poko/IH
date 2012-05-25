//
//  RewalkHikeDelegate.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/19/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Hike.h"

typedef void (^RewalkHikeHandler)(bool result, Hike *hike);

@interface RewalkHikeDelegate : NSObject <NSURLConnectionDelegate>{
    RewalkHikeHandler _handler;
}

-(id) initWithHandler:(RewalkHikeHandler)handler;

@end
