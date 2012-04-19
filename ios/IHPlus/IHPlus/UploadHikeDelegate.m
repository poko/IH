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
    // TODO Send back to handler?
    NSLog(@"Upload Connection failed! Error - %@ %@", [error localizedDescription], [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // parse response data
    NSLog(@"Succeeded! Received %d bytes of data",[receivedData length]);
    if ([receivedData length] > 0){
       //TODO parse out success
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
