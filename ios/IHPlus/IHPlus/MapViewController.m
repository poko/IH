//
//  MapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MapViewController.h"
#import "GetDirectionsDelegate.h"
#import "AppDelegate.h"

@implementation MapViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
 }
 */
-(void)viewDidAppear:(BOOL)animated
{
    NSLog(@"mapview appears");
    //MKMapView *map = [(AppDelegate *)[[UIApplication sharedApplication] delegate] map];//[[(AppDelegate *) [[UIApplication sharedApplication] delegate] map];
    if (_mapView != nil){
//        NSLog(@"self.view? %@", self.view);
//        if ([_mapView isDescendantOfView:self.view])
//            NSLog(@"not descendant" );
//        else
//            NSLog(@"is descendant");
            
        //[_mapView setHidden:NO];
        //[self.view addSubview: _mapView];
        [self.view insertSubview:_mapView belowSubview:_inputHolder];
    }
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    // set map
    [(AppDelegate *)[[UIApplication sharedApplication] delegate] setMap:_mapView];
    _mapView.delegate = self;
    [_endAddress setDelegate:self];
    [_startAddress setDelegate:self];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque; 
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];

    CLLocationCoordinate2D annotationCoord;
    
    annotationCoord.latitude = 47.640071;
    annotationCoord.longitude = -122.129598;
    
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = annotationCoord;
    [_mapView addAnnotation:annotationPoint];
}
 

-(void) mapView:(MKMapView *)mapView didAddOverlayViews:(NSArray *)overlayViews{
    NSLog(@"overlays added");
}

- (void) mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation{
    
    NSLog(@"User location lat: %@", userLocation.location );
    //MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance (userLocation.location.coordinate, 200, 200);
    //[mapView setRegion:region animated:YES];
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id )overlay
{
    MKOverlayView* overlayView = nil;
    
    if(overlay == _routeLine)
    {
        NSLog(@"trying to add route line");
        //if we have not yet created an overlay view for this overlay, create it now.
    if (_routeLineView == nil){
        _routeLineView = [[MKPolylineView alloc] initWithPolyline:_routeLine];
        _routeLineView.fillColor = [UIColor redColor];
        _routeLineView.strokeColor = [UIColor redColor];
        _routeLineView.lineWidth = 3;
    }
    overlayView = _routeLineView;
    }
    
    return overlayView;
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}



-(void)showLoadingDialog
{
    //loading dialog
    if (_loadingIndicator == nil){
        _loadingIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [_loadingIndicator setHidesWhenStopped:YES];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 60.0, 60.0);
        _loadingIndicator.center = self.view.center;
    }
    [self.view addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
}

-(void)hideLoadingDialog:(NSString *) error
{
    
}

-(float) getRandomOffset
{
    float min = .0005f;
    float max = .005f;
    //double num = Math.random() * (max - min);
    float diff = max - min;
    float offset =  (((float) (arc4random() % ((unsigned)RAND_MAX + 1)) / RAND_MAX) * diff) + min;
    NSLog(@"offset: %f", offset);
    //int i = (offset / .0001) % 2 == 0 ? 1 : -1;
    int i = 1;
    int temp = offset / .0001;
    if (temp % 2 != 0)
        i = -1;
    NSLog(@"i: %i", i);
    return offset * i;
}

-(void) getDirectionsFrom:(NSString *) from to:(NSString *) to
{
    NSLog(@"getting directions from : %@ to: %@", from, to);
    NSString *url = [NSString stringWithFormat:@"http://maps.google.com/maps?output=kml&saddr=%@&daddr=%@", 
                     [from stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding], 
                     [to stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSLog(@"Sending to url %@", url);
                     
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
    NSLog(@"url: %@", req.URL);
    //GetDirectionsDelegate *connDelegate = [[GetDirectionsDelegate alloc] init];
    GetDirectionsDelegate *connDelegate = [[GetDirectionsDelegate alloc] initWithHandler:^(NSMutableArray *points, NSError *error) {
        NSLog(@"handler gets: %i", [points count]);
        _callCount++;
        [_pathPoints addObjectsFromArray:points];
        NSLog(@"compeltion handler!! %i", _callCount);
        NSLog(@"we have this many points: %i", [_pathPoints count]);
        if (_callCount == 2){
            NSLog(@"should be drawing now, %i", [_pathPoints count]);
            [_loadingIndicator stopAnimating];
            // draw overlay
            CLLocationCoordinate2D *points = malloc([_pathPoints count] * sizeof(CLLocationCoordinate2D));
            for (int i = 0; i < [_pathPoints count]; i++){
                points[i] = [(CLLocation *)[_pathPoints objectAtIndex:i ] coordinate];//.coordinate;
            }
            MKPolyline *line = [MKPolyline polylineWithCoordinates:points count:[_pathPoints count]];
            _routeLine = line;
            MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance([line coordinate], 300, 300);
            [_mapView setRegion:region animated:YES];
            [_mapView addOverlay:line];
            free(points);
        }
        
    }];
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:connDelegate];
    if (connection) {
        // Create the NSMutableData to hold the received data.
        // receivedData is an instance variable declared elsewhere.
        // TODO // ?receivedData = [NSMutableData data];
    } else {
        NSLog(@"connection failed");
//        [_loadingIndicator stopAnimating];
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
//        [alert show];
    }
}

#pragma mark IBActions
-(IBAction)hitTrail:(id)sender
{
    NSLog(@"hit trail");
    // clear out any previous data
    _callCount = 0;
    _pathPoints = [NSMutableArray array];
    // check that both fields have values
    NSString *start = [_startAddress text];
    NSString *end = [_endAddress text];
    if ([start length] > 0 && [end length] > 0){
        // start making calls!
        // fwd geocode start location
        [self showLoadingDialog];
        //TODO - check if "current location"
        // geocode search
        if (!_geocoder){
            _geocoder = [[CLGeocoder alloc] init];
        }
        [_geocoder geocodeAddressString:start completionHandler:
         ^(NSArray* placemarks, NSError* error){
             if ([placemarks count] > 0){
                 CLPlacemark *placemark = [placemarks objectAtIndex:0];
                 CLLocationDegrees latitude = placemark.location.coordinate.latitude;
                 CLLocationDegrees longitude = placemark.location.coordinate.longitude;
                 // add random offset
                 float randLat = [self getRandomOffset]+latitude;
                 float randLng = [self getRandomOffset]+longitude;
                 
                 NSLog(@" lat: %f", latitude);
                 NSLog(@" lng: %f", longitude);
                 NSLog(@"Rand lat: %f", randLat);
                 NSLog(@"Rand lng: %f", randLng);
                 // reverse geocode the random point //TODO ??
                 //CLLocation *randLoc = [[CLLocation alloc] initWithLatitude:randLat longitude:randLng];
                 NSString *randPoint = [NSString stringWithFormat:@"%f,%f",randLat, randLng];
                 [self getDirectionsFrom:start to:randPoint];
                 [self getDirectionsFrom:randPoint to:end];
//                 [_geocoder reverseGeocodeLocation:randLoc completionHandler:^(NSArray *placemarks, NSError *error) {
//                    
//                     NSLog(@"reverse geocoding done. %i", placemarks.count);
//                     if ([placemarks count] > 0){
//                         CLPlacemark *randPlacemark = [placemarks objectAtIndex:0];
//                         NSLog(@"Placemark : %@", randPlacemark);
//                         NSLog(@"Placemark name: %@", randPlacemark.name);
//                         NSLog(@"Placemark locality: %@", randPlacemark.locality);
//                         NSLog(@"Placemark addy dict: %@", randPlacemark.addressDictionary);
//                         NSString *randPoint = [NSString stringWithFormat:@"%@ %@", [randPlacemark.addressDictionary objectForKey:@"Name"], [randPlacemark.addressDictionary objectForKey:@"ZIP"]];
//                         NSLog(@"rand point: %@", randPoint);
//                                                
//                         // make directions calls to start->random and random->start
//                     }
//                     else{
//                         NSLog(@"Failed to reverse geocode.");
//                     }
//                 }];
//                 NSString *url = [NSString stringWithFormat:@"http://ecoarttech.net/ih_plus/scripts/getHikesByLocation.php?latitude=%f&longitude=%f", latitude, longitude];
//                 NSLog(@"Sending to url %@", url);
//                 
//                 
//                 NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];    
//                 NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:self];
//                 if (connection) {
//                     // Create the NSMutableData to hold the received data.
//                     // receivedData is an instance variable declared elsewhere.
//                     receivedData = [NSMutableData data];
//                 } else {
//                     NSLog(@"connection failed");
//                     [_loadingIndicator stopAnimating];
//                     UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
//                     [alert show];
//                 }
             }
             else{
                 NSLog(@"Nothing returned for placemarks search");
                 // TODO
//                 [_loadingIndicator stopAnimating];
//                 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Invalid Location" message:[NSString stringWithFormat:@"Sorry, %@ isn't a location we can find", [searchBar text]] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
//                 [alert show];
             } 
         }];

    }
}


-(IBAction)currentLocation:(id)sender
{
    NSLog(@"current loc");
    [_startAddress setText:@"Current Location"];
}


#pragma mark TextField Delegate

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == _startAddress){
        NSLog(@"start next");
        NSInteger nextTag = textField.tag + 1;
        // Try to find next responder
        UIResponder* nextResponder = [textField.superview viewWithTag:nextTag];
        if (nextResponder) {
            // Found next responder, so set it.
            [nextResponder becomeFirstResponder];
        } else {
            // Not found, so remove keyboard.
            [textField resignFirstResponder];
        }
    }
    else if (textField == _endAddress){
        NSLog(@"end go");
        [textField resignFirstResponder];
        [self hitTrail:textField];
    }
    return NO;
}


@end
