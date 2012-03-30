//
//  GetDirectionsDelegate.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/26/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "GetDirectionsDelegate.h"

@implementation GetDirectionsDelegate

-(id) initWithHandler:(DirectionsCompletionHandler)handler
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
    NSLog(@"didReceiveResponse");
    receivedData = [NSMutableData data];
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"didReceiveData");
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // inform the user 
    // TODO [_loadingIndicator stopAnimating];
    NSLog(@"Connection failed! Error - %@ %@", [error localizedDescription], [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // do something with the data
    // receivedData is declared as a method instance elsewhere
    // TODO [_loadingIndicator stopAnimating];
    NSLog(@"Succeeded! Received %d bytes of data",[receivedData length]);
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:receivedData];
    [parser setDelegate:self];
    [parser parse];
}

# pragma mark XML delegate
BOOL savingChars = NO;
NSMutableString *coordStr;
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    //NSLog(@"starting element: %@", elementName);
    if ([elementName isEqualToString:@"LineString"]){
        NSLog(@"found line string:");
        savingChars = YES;
        coordStr = [NSMutableString string];
    }
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    //NSLog(@"found chars");
    if (savingChars){
        NSLog(@"now we save? %@", string);
        [coordStr appendString:string];
    }
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([elementName isEqualToString:@"LineString"]){
        NSLog(@"ended line string: %@" , coordStr);
        savingChars = NO;
        // parse coords into points
        NSMutableArray *locCoords = [NSMutableArray array];
        NSArray *coords = [coordStr componentsSeparatedByString:@" "];
        //CLLocationCoordinate2D locCoords[[coords count]];
        //CLLocationCoordinate2D *locCoords = malloc(([coords count] - 1) * sizeof(CLLocationCoordinate2D));
        NSLog(@"coords count: %i" , [coords count]);
        for (int i = 0; i < [coords count]; i++){
            NSArray *lngLat = [[coords objectAtIndex:i] componentsSeparatedByString:@","];
            //NSLog(@"lngLat: %@" , lngLat);
            if ([lngLat count] > 1){
                CLLocationDegrees latitude = [[lngLat objectAtIndex:1] doubleValue];
                CLLocationDegrees longitude = [[lngLat objectAtIndex:0] doubleValue];
                CLLocation *loc = [[CLLocation alloc] initWithLatitude:latitude longitude:longitude];
                //CLLocationCoordinate2D loc = CLLocationCoordinate2DMake(latitude, longitude);
                [locCoords addObject:loc];
                //locCoords[i] = loc;
            }
        }
        // pass points back to mapView.
        _handler(locCoords, nil);
    }
}


@end