//
//  VistaActionsDelegate.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "VistaActionsDelegate.h"

@implementation VistaActionsDelegate


-(id) initWithHandler:(VistaActionsHandler)handler
{
    id i = [super init];
    _handler = handler;
    return i;
}

#pragma mark - Connection delgate (vista actions)

NSMutableData *vistaActionsData;
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    // This method is called when the server has determined that it
    // has enough information to create the NSURLResponse.
    
    // It can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
    
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"get actions - didReceiveResponse");
    vistaActionsData = [NSMutableData data];
    [vistaActionsData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"get actions - didReceiveData");
    [vistaActionsData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // inform the user 
    _handler(nil, @"Unable to connect to server");
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // parse response data
    NSLog(@"Succeeded! Received %d bytes of data",[vistaActionsData length]);
    //TODO - set actions for vistas.
    NSError *error; 
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:vistaActionsData options:kNilOptions error:&error];
    if (error != nil){
        NSLog(@"Here is the error: %@", error); 
        _handler(nil, @"Server did not return valid data");
        return;
    }
    NSArray *actions = [json objectForKey:@"vista_actions"];
    _handler(actions, nil);
}

@end
