//
//  UploadHikeDelegate.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/13/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^UploadCompletionHandler)(bool result, NSString *msg);

@interface UploadHikeDelegate : NSObject <NSURLConnectionDelegate>{
    UploadCompletionHandler _handler;
}


-(id) initWithHandler:(UploadCompletionHandler)handler;

@end
