//
//  RewalkHikeDelegate.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/19/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "RewalkHikeDelegate.h"

@implementation RewalkHikeDelegate

-(id) initWithHandler:(RewalkHikeHandler)handler
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
    NSLog(@"Rewalk - didReceiveResponse");
    receivedData = [NSMutableData data];
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"Rewalk - didReceiveData");
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // inform the user 
    _handler(false, nil);
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // parse response data
    NSLog(@"Rewalk Succeeded! Received %d bytes of data",[receivedData length]);
    if ([receivedData length] > 0){
        NSError *error;
        NSString *receivedStr = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
        NSString *escaped = [receivedStr stringByReplacingOccurrencesOfString:@"\n" withString:@""];
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[escaped dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:&error];
        if (error != nil){
            NSLog(@"Here is the error: %@", error);
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Server Error" message:@"There was an error with the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
        }
        NSLog(@"Here is the escaped response: %@", json);
        NSDictionary *hikeJson = [json objectForKey:@"hike"];
        NSLog(@"hike json: %@", hikeJson);
        Hike *hike = [Hike initWithDictionary:hikeJson];
        _handler(true, hike);
    }
    else{
        _handler(false, nil);
    }
}

@end
