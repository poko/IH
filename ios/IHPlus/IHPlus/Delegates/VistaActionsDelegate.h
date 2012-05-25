//
//  VistaActionsDelegate.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/25/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^VistaActionsHandler)(NSArray *actions, NSString *error);

@interface VistaActionsDelegate : NSObject <NSURLConnectionDelegate>{
    VistaActionsHandler _handler;
}

-(id) initWithHandler:(VistaActionsHandler)handler;

@end
