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
    NSLog(@"Directions Succeeded! Received %d bytes of data",[receivedData length]);
   // NSLog(@"we got: %@ ",[[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding]);
    if ([receivedData length] > 0){
        NSError *error;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:receivedData options:kNilOptions error:&error];
        if (error != nil){
            NSLog(@"Here is the error: %@", error);
            _handler(nil, @"Invalid Directions between locations");
            return;
        }
        NSString *status = [json objectForKey:@"status"];
        if ([status isEqualToString:@"OK"]){
            NSArray *routes = [json objectForKey:@"routes"];
            // get first route
            NSDictionary *route = [routes objectAtIndex:0];
            NSLog(@"route: %@", route);
            NSArray *legs = [route objectForKey:@"legs"];
            NSLog(@"legs: %@", legs);
            NSArray *steps = [[legs objectAtIndex:0] objectForKey:@"steps"];
            NSLog(@"steps: %@", steps);
            NSMutableArray *locCoords = [NSMutableArray array];
            for (int i = 0; i < [steps count]; i++){
                NSDictionary *step = [steps objectAtIndex:i];
                // add each start point
                NSDictionary *latLng = [step objectForKey:@"start_location"];
                NSLog(@"latlng: %@", latLng);
                CLLocationDegrees latitude = [[latLng objectForKey:@"lat"] doubleValue];
                CLLocationDegrees longitude = [[latLng objectForKey:@"lng"] doubleValue];
                CLLocation *loc = [[CLLocation alloc] initWithLatitude:latitude longitude:longitude];
                [locCoords addObject:loc];
            }
            // add last end location
            NSDictionary *step = [steps objectAtIndex:[steps count]-1];
            NSLog(@"last step: %@", step);
            NSDictionary *latLng = [step objectForKey:@"end_location"];
            CLLocationDegrees latitude = [[latLng objectForKey:@"lat"] doubleValue];
            CLLocationDegrees longitude = [[latLng objectForKey:@"lng"] doubleValue];
            CLLocation *loc = [[CLLocation alloc] initWithLatitude:latitude longitude:longitude];
            [locCoords addObject:loc];
            // send data back
            foundDirs = YES;
            _handler(locCoords, nil);
        }
        //NSXMLParser *parser = [[NSXMLParser alloc] initWithData:receivedData];
        //[parser setDelegate:self];
        //[parser parse];
    }
    else{
        _handler(nil, @"Invalid Directions between locations");
    }
}

# pragma mark XML delegate
BOOL savingChars = NO;
BOOL foundDirs = NO;
NSMutableString *coordStr;
-(void) parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError{
    NSLog(@"error: %@", parseError);
    _handler(nil, @"Error with directions from Google.");
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    NSLog(@"starting element: %@", elementName);
    if ([elementName isEqualToString:LINE_STRING]){
        NSLog(@"found line string:");
        savingChars = YES;
        coordStr = [NSMutableString string];
    }
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    NSLog(@"found chars");
    if (savingChars){
        NSLog(@"now we save");
        [coordStr appendString:string];
    }
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([elementName isEqualToString:LINE_STRING]){
        NSLog(@"ended line string element");
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
