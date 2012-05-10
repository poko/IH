//
//  UploadHikeDelegate.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/13/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UploadHikeDelegate.h"

@implementation UploadHikeDelegate

-(id) initWithHandler:(UploadCompletionHandler)handler
{
    id i = [super init];
    _handler = handler;
    return i;
}

#pragma mark - Connection delgate

NSMutableData *receivedData;
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    // This method is called when the server has determined that it
    // has enough information to create the NSURLResponse.
    
    // It can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
    
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"Upload - didReceiveResponse");
    receivedData = [NSMutableData data];
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"Upload - didReceiveData");
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // inform the user 
    _handler(false, @"Failed to connect to server");
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // parse response data
    NSLog(@"Succeeded! Received %d bytes of data",[receivedData length]);
    NSLog(@"Succeeded! data: %@",[[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding]);
    if ([receivedData length] > 0){
       //parse out success
        NSError *error;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:receivedData options:kNilOptions error:&error];
        if (error == nil){
            NSString *success = [json objectForKey:@"result"];
            bool s = [success isEqualToString:@"true"];
            _handler(s, nil);
            return;
        }
        _handler(false, [error description]);
    }
    else{
        _handler(false, @"Failed to upload hike");
    }
}

@end
