//
//  GetDirectionsDelegate.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/26/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

typedef void (^DirectionsCompletionHandler)(NSMutableArray *points, NSString *error);

@interface GetDirectionsDelegate : NSObject <NSURLConnectionDelegate, NSXMLParserDelegate>{    DirectionsCompletionHandler _handler;
}


-(id) initWithHandler:(DirectionsCompletionHandler)handler;

@end
