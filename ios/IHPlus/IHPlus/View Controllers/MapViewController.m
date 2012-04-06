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
#import "ScenicVista.h"

#define RANDOM_INT(min, max) (min + arc4random() % ((max + 1) - min))

@implementation MapViewController

NSMutableData *vistaActionsData;

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

-(void)viewDidAppear:(BOOL)animated
{
    if (_mapView != nil){
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
    [_inputHolder setBackgroundColor:[[UIColor alloc] initWithPatternImage:[UIImage imageNamed:@"black_gradient.png"]]];
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
            _routeLineView.fillColor = [UIColor blueColor];
            _routeLineView.strokeColor = [UIColor blueColor];
            _routeLineView.lineWidth = 3;
        }
        overlayView = _routeLineView;
    }
    return overlayView;
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // TODO Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void) drawVistas
{
    NSLog(@"drawing this many vistas: %i", [[_hike vistas] count]);
    for (int i = 0; i < [[_hike vistas] count]; i++){
        ScenicVista *vista = [[_hike vistas] objectAtIndex:i];
        MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
        [annotationPoint setCoordinate:[[vista location] coordinate]];
        [annotationPoint setTitle:[vista prompt]];
        [_mapView addAnnotation:annotationPoint];
    }
}



-(void)showLoadingDialog
{
    //loading dialog
    if (_loadingIndicator == nil){
        _loadingIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [_loadingIndicator setHidesWhenStopped:YES];
        [_loadingIndicator setBackgroundColor:[UIColor colorWithWhite:0 alpha:.5]];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 320.0, 480.0);
        _loadingIndicator.center = self.view.center;
    }
    [self.view addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
}

-(void)hideLoadingDialog:(NSString *) error
{
    [_loadingIndicator stopAnimating];
    if (error != nil){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Creating Hike" message:[NSString stringWithFormat:@"There was an error creating the hike: %@", error]
             delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
    }
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

int midpoint;
-(void) getDirectionsFrom:(NSString *) from to:(NSString *) to
{
    //NSLog(@"getting directions from : %@ to: %@", from, to);
    NSString *url = [NSString stringWithFormat:@"http://maps.google.com/maps?output=kml&saddr=%@&daddr=%@", 
                     [from stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding], 
                     [to stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSLog(@"Sending to url %@", url);
                     
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
    NSLog(@"url: %@", req.URL);
    GetDirectionsDelegate *connDelegate = [[GetDirectionsDelegate alloc] initWithHandler:^(NSMutableArray *points, NSString *error) {
        NSLog(@"handler gets: %i", [points count]);
        if (error != nil){
            [self hideLoadingDialog:error];
            return;
        }
        _callCount++;
        [_pathPoints addObjectsFromArray:points];
        NSLog(@"compeltion handler!! %i", _callCount);
        NSLog(@"we have this many points: %i", [_pathPoints count]);
        if (_callCount == 1){
            midpoint = ([_pathPoints count] - 1);
        }
        if (_callCount == 2){
            NSLog(@"should be drawing now, %i", [_pathPoints count]);
            // Create the Hike object!
            _hike = [[Hike alloc] init];
            //[_loadingIndicator stopAnimating];
            // draw overlay
            CLLocationCoordinate2D *pointsLine = malloc([_pathPoints count] * sizeof(CLLocationCoordinate2D));
            for (int i = 0; i < [_pathPoints count]; i++){
                pointsLine[i] = [(CLLocation *)[_pathPoints objectAtIndex:i ] coordinate];
                //set point in Hike object for later uploadingz.
                [_hike addPoint:(CLLocation *)[_pathPoints objectAtIndex:i]];
            }
            MKPolyline *line = [MKPolyline polylineWithCoordinates:pointsLine count:[_pathPoints count]];
            _routeLine = line;
            MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance([[_pathPoints objectAtIndex:0 ] coordinate], 400, 400);
            [_mapView setRegion:region animated:YES];
            [_mapView addOverlay:line];
            free(pointsLine);
            // hide the input view
            [_inputHolder setHidden:YES];
            UIBarButtonItem *createButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(clickedCreateButton:)];
            [[self navigationItem] setLeftBarButtonItem:createButton];
            // generate random scenic vistas
            // make end point and mid point SVs
            [_hike addVista:(CLLocation *)[_pathPoints objectAtIndex:midpoint]];
            [_hike addVista:(CLLocation *)[_pathPoints objectAtIndex:([_pathPoints count] - 1)]];
            // 1-3 additional vista points per half
            int vistaAmount = RANDOM_INT(1, 3);
            int randIndex;
            for (int i = 0; i < vistaAmount; i++){
                randIndex = RANDOM_INT(0, midpoint);
                [_hike addVista:(CLLocation *)[_pathPoints objectAtIndex:randIndex]];
            }
            vistaAmount = RANDOM_INT(1, 3);
            for (int i = 0; i < vistaAmount; i++){
                randIndex = RANDOM_INT(midpoint, ([_pathPoints count] - 1));
                [_hike addVista:(CLLocation *)[_pathPoints objectAtIndex:randIndex]];
            }
            // TODO - download vista actions
            NSString *url = [NSString stringWithFormat:@"http://localhost:8888/IHServer/getVistaAction.php?amount=%i", [[_hike vistas] count]];
            NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];    
            NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:self];
            if (!connection) {
                // Create the NSMutableData to hold the received data.
                // receivedData is an instance variable declared elsewhere.
                vistaActionsData = [NSMutableData data];
            } else {
                NSLog(@"connection failed");
            }
                
        }
        
    }];
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:connDelegate];
    if (!connection) {
        NSLog(@"connection failed");
        [self hideLoadingDialog:@"Could not connect to server"];
    }
}

- (IBAction) clickedCreateButton:(id)sender{
    NSLog(@"clicked create!");
    //TODO check that user hasn't completed any vistas, if they have, warn.
    //TODO - clear hike
    [_inputHolder setHidden:NO];
    [[self navigationItem] setLeftBarButtonItem:nil];
}

#pragma mark IBActions
-(IBAction)hitTrail:(id)sender
{
    NSLog(@"hit trail");
    [_startAddress resignFirstResponder];
    [_endAddress resignFirstResponder];
    // remove map overlay
    [_mapView removeOverlays:[_mapView overlays]];
    _routeLine = nil;
    _routeLineView = nil;
    // remove annotations
    NSMutableArray *toRemove = [NSMutableArray array];
    for (id annotation in _mapView.annotations)
        if (annotation != _mapView.userLocation)
            [toRemove addObject:annotation];
    [_mapView removeAnnotations:toRemove];
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
             }
             else{
                 NSLog(@"Nothing returned for placemarks search");
                 [self hideLoadingDialog:@"Not a valid starting location"];
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

#pragma mark - Connection delgate (vista actions)

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    // This method is called when the server has determined that it
    // has enough information to create the NSURLResponse.
    
    // It can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
    
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"didReceiveResponse");
    vistaActionsData = [NSMutableData data];
    [vistaActionsData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"didReceiveData");
    [vistaActionsData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // inform the user 
    [_loadingIndicator stopAnimating];
    NSLog(@"Connection failed! Error - %@ %@", [error localizedDescription], [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [_loadingIndicator stopAnimating];
    // parse response data
    NSLog(@"Succeeded! Received %d bytes of data",[vistaActionsData length]);
    //TODO - set actions for vistas.
    NSError *error; 
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:vistaActionsData options:kNilOptions error:&error];
    if (error != nil){
        NSLog(@"Here is the error: %@", error); 
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Server Error" message:@"There was an error with the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
    }
    NSArray *actions = [json objectForKey:@"vista_actions"];
    if ([actions count] != [[_hike vistas] count]){
        NSLog(@"something went terribly wrong!");
        return;
    }
    int i = 0;
    for (ScenicVista *vista in [_hike vistas]){
        NSDictionary *action = [actions objectAtIndex:i];
        [vista setActionId:[action objectForKey:@"action_id"]];
        [vista setActionType:[action objectForKey:@"action_type"]];
        [vista setPrompt:[action objectForKey:@"verbiage"]];
        i++;
    }
    [self drawVistas];
}


@end
