//
//  GetDirectionsDelegate.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/26/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "GetDirectionsDelegate.h"
#define LINE_STRING @"LineString"

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
    receivedData = [NSMutableData data];
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // inform the user 
    _handler(nil, @"Failed to connect to server");
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // parse response data
    //NSLog(@"Directions Succeeded! Received %d bytes of data",[receivedData length]);
    if ([receivedData length] > 0){
        NSXMLParser *parser = [[NSXMLParser alloc] initWithData:receivedData];
        [parser setDelegate:self];
        [parser parse];
    }
    else{
        _handler(nil, @"Invalid Directions between locations");
    }
}

# pragma mark XML delegate
BOOL savingChars = NO;
BOOL foundDirs = NO;
NSMutableString *coordStr;
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    //NSLog(@"starting element: %@", elementName);
    if ([elementName isEqualToString:LINE_STRING]){
        //NSLog(@"found line string:");
        savingChars = YES;
        coordStr = [NSMutableString string];
    }
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    //NSLog(@"found chars");
    if (savingChars){
       // NSLog(@"now we save");
        [coordStr appendString:string];
    }
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([elementName isEqualToString:LINE_STRING]){
        //NSLog(@"ended line string element");
        savingChars = NO;
        // parse coords into points
        NSMutableArray *locCoords = [NSMutableArray array];
        NSArray *coords = [coordStr componentsSeparatedByString:@" "];
        //CLLocationCoordinate2D locCoords[[coords count]];
        //CLLocationCoordinate2D *locCoords = malloc(([coords count] - 1) * sizeof(CLLocationCoordinate2D));
        //NSLog(@"coords count: %i" , [coords count]);
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
        foundDirs = YES;
        _handler(locCoords, nil);
    }
}

- (void)parserDidEndDocument:(NSXMLParser *)parser
{
    //NSLog(@"finished parsing doc: %@", coordStr);
    if (!foundDirs){
        // here's where there weren't valid directions returned, silly!
        _handler(nil, @"Invalid directions");
    }
}


@end
