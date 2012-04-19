//
//  MapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MapViewController.h"
#import "GetDirectionsDelegate.h"
#import "RewalkHikeDelegate.h"
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
    NSLog(@"MapViewController low mem");
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
    [_endAddress setDelegate:self]; [_endAddress setText:@"1300 bob harrison 78702"];
    [_startAddress setDelegate:self]; [_startAddress setText:@"1200 bob harrison austin, tx"];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque; 
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
    [_inputHolder setBackgroundColor:[[UIColor alloc] initWithPatternImage:[UIImage imageNamed:@"black_gradient.png"]]];
    // check for region monitoring
    if ([CLLocationManager regionMonitoringAvailable] && [CLLocationManager regionMonitoringEnabled]){
        NSLog(@"we are good to go!");
        _locMgr = [[CLLocationManager alloc] init];
        [_locMgr setDelegate:self];
        _monitoredRegions = [NSMutableArray array];
    }
    else{
        //TODO
        NSLog(@"OH NOESRLKEJRWOI!! This app won't work!!!");
    }
    // keyboard handling
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) 
                                                 name:UIKeyboardWillShowNotification object:self.view.window]; 
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) 
                                                 name:UIKeyboardWillHideNotification object:self.view.window]; 

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
    
    NSLog(@"User location : %@", userLocation.location );
    NSLog(@"how many regions we tracking? %i", [[_locMgr monitoredRegions] count]);
    //[userLocation location] distanceFromLocation:<#(const CLLocation *)#>
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
            UIColor *lineColor = [UIColor colorWithRed:(33.0/255.0) green:(217.0/255.0) blue:(252.0/255.0) alpha:.66];
            _routeLineView.fillColor = lineColor;
            _routeLineView.strokeColor = lineColor;
            _routeLineView.lineWidth = 4;
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
    NSLog(@"view unloaded"); // TODO this may be removed when we stop debugging? (creating lots of monitoring regions)
    // remove any pending proximity alerts
    if (_monitoredRegions != nil){
        for (CLRegion *region in _monitoredRegions){
            [_locMgr stopMonitoringForRegion:region];
        }
    }
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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
        NSLog(@"Vista at coord: %@", [vista location]);
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

int midpoint;// = 1; //TODO!!
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
//        if (_callCount == 1){
//            midpoint = ([_pathPoints count] - 1);
//            [self getDirectionsFrom:to to:[_endAddress text]];
//        }
//        if (_callCount == 2){
            NSLog(@"should be drawing now, %i", [_pathPoints count]);
            // Create the Hike object!
            _hike = [[Hike alloc] init];
            [_hike setOriginal:@"true"];
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
            // if we don't have enough points to select randomly, make all points vistas
            if ([_pathPoints count] - 2 < 8){
                for (int i = 1; i < [_pathPoints count]; i++){
                    [_hike addVista:[_pathPoints objectAtIndex:i]];
                }
            }
            else{
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
                NSLog(@"connection to get actions failed");
            }
                
//        }
        
    }];
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:connDelegate];
    if (!connection) {
        NSLog(@"connection failed");
        [self hideLoadingDialog:@"Could not connect to server"];
    }
}

-(void) completeCurrentVista
{
    [_currentVista setComplete:YES];
    _currentVista = nil;
    [_promptHolder setHidden:YES];
    // check if we can enable upload button
    if ([_hike eligibleForUpload])
        [_uploadButton setEnabled:YES];
    //TODO - remove region tracking!
    // if all the vistas are complete, show the modal automagically. 
    if ([_hike isComplete]){
        [self performSegueWithIdentifier:@"UploadHike" sender:self];
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
    // remove any pending proximity alerts
    if (_monitoredRegions != nil){
        for (CLRegion *region in _monitoredRegions){
            [_locMgr stopMonitoringForRegion:region];
        }
    }
    
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
                 // TODO asdfasdfa 
                 [self getDirectionsFrom:start to:end];
//                 [self getDirectionsFrom:start to:randPoint];
//                 [self getDirectionsFrom:randPoint to:end];
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

-(IBAction)continueClicked:(id)sender
{
    //start modal for inputing .. ? maybe have modal popup directly? 
    if (_currentVista != nil){
        switch ([_currentVista getActionType]){
            case MEDITATE:{
                NSLog(@"MEDITATE!");
                [self completeCurrentVista];
                break;
            }
            case NOTE: {
                NSLog(@"NOTE!");
                // popup modal for note taking
                [self performSegueWithIdentifier:@"NoteModal" sender:self];
                break;
            }
            case TEXT: {
                NSLog(@"TEXT!");
                // popup modal for texting logic
                [self performSegueWithIdentifier:@"TextModal" sender:self];
                break;
            }
            case PHOTO: {
                NSLog(@"PHOTO!");
                // Create image picker controller
                UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
                
                // Set source to the camera
                [imagePicker setSourceType:UIImagePickerControllerSourceTypeCamera];
                
                // Delegate is self
                [imagePicker setDelegate:self];
                
                // Allow editing of image ?
                [imagePicker setAllowsEditing:NO];
                
                // Show image picker
                [self presentModalViewController:imagePicker animated:YES];
                break;
            }
        }
    }
}

-(IBAction)uploadHike:(id) sender{
    [self performSegueWithIdentifier:@"UploadHike" sender:self];
}

# pragma  mark modal delegates
- (void)noteModalController:(NoteModalController *)controller done:(NSString *) note
{
    [controller dismissViewControllerAnimated:YES completion:^{
        [_currentVista setNote:note];
        NSLog(@"and we set the note in the mapview: %@", note);
        [self completeCurrentVista];
    }];
}

- (void)uploadModalController:(UploadHikeController *)controller done:(NSString *) error
{
    NSLog(@"called uploadModalController done");
    [controller dismissViewControllerAnimated:YES completion:^{
        NSLog(@"and hike should have uploaded! %@", error);
        if (error != nil){
            // TODO boo!
            NSLog(@" boooo error back in mapview");
        }
        else{
            // TODO success!
            NSLog(@" no error back in mapview");
        }
    }];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{ //TODO cleanup logic
    if ([[segue identifier] isEqualToString:@"NoteModal"]){
        NoteModalController *modal = [segue destinationViewController];
        [modal setPromptText:[_currentVista prompt]];
        [modal setVcDelegate:self];
    }
    else if ([[segue identifier] isEqualToString:@"TextModal"]){
        TextModalController *modal = [segue destinationViewController];
        [modal setPromptText:[_currentVista prompt]];
        [modal setVcDelegate:self];
    }
    else if ([[segue identifier] isEqualToString:@"UploadHike"]){
        UploadHikeController *modal = [segue destinationViewController];
        [modal setHike:_hike];
        [modal setVcDelegate:self];
    }
}

#pragma mark image picker delegate

- (void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    // Access the uncropped image from info dictionary
    UIImage *image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
    
    // Save image
    UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
    //TODO - mark vista as complete, save location of photo for uploadings
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

#pragma mark geofencing

- (BOOL)registerRegionWithCircularOverlay:(CLLocationCoordinate2D)coord andIdentifier:(NSString*)identifier
{
    // Do not create regions if support is unavailable or disabled.
    if ( ![CLLocationManager regionMonitoringAvailable] ||
        ![CLLocationManager regionMonitoringEnabled] )
        return NO;
    
    // If the radius is too large, registration fails automatically,
    // so clamp the radius to the max value.
    CLLocationDegrees radius = 3;
    if (radius > _locMgr.maximumRegionMonitoringDistance)
        radius = _locMgr.maximumRegionMonitoringDistance;
    
    // Create the region and start monitoring it.
    CLRegion* region = [[CLRegion alloc] initCircularRegionWithCenter:coord
                                                               radius:radius identifier:identifier];
    [_monitoredRegions addObject:region];
    [_locMgr startMonitoringForRegion:region
                     desiredAccuracy:kCLLocationAccuracyBest];
    
    return YES;
}

- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error
{
    NSLog(@"failed to monitor region!!, %@", error);
}

- (void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    NSLog(@"entered region! %@", [region identifier]);
    // show action view
    [_promptHolder setHidden:NO];
    _currentVista = [_hike getVistaById:[region identifier]];
    [_prompt setText:[_currentVista prompt]];
}

- (void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region
{
    NSLog(@"exited region!");
}

-(void)locationManager:(CLLocationManager *)manager didStartMonitoringForRegion:(CLRegion *)region
{
    NSLog(@"monitoring region: %@", [region identifier]);
}

#pragma mark - rewalk hike
- (void) rewalkHike:(NSString *)hikeId
{
    [self showLoadingDialog];
    // make server call
    NSString *url = [NSString stringWithFormat:@"http://localhost:8888/IHServer/getHike.php?hike_id=%@", hikeId];
    NSLog(@"Sending to url %@", url);
    RewalkHikeDelegate *rewalkDelegate = [[RewalkHikeDelegate alloc] initWithHandler:^(bool result, Hike *hike) {
        NSLog(@"tralala back in the map view with a hike! %@", hike);
        [self hideLoadingDialog:nil];
    }];
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]]; 
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:rewalkDelegate];
    if (!connection) {
        NSLog(@"connection failed");
        [self hideLoadingDialog:@"There was an error connecting to the server."];
    }

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
        //TODO [vista setActionType:[action objectForKey:@"action_type"]];
        [vista setActionType:@"text"];
        [vista setPrompt:[action objectForKey:@"verbiage"]];
        [self registerRegionWithCircularOverlay:[[vista location] coordinate] andIdentifier:[vista actionId]];
        i++;
    }
    // enable geofences for vistas
    [self drawVistas];
}


#pragma mark keyboard handling

UIButton *dummy;
- (void)keyboardWillShow:(NSNotification *)notif {
    // setup dummy view to handle clicks for hiding keyboard
    dummy = [[UIButton alloc] initWithFrame:CGRectMake(0, 90, 320, 110)];
    [self.view insertSubview:dummy aboveSubview:_mapView];
    [dummy addTarget:self action:@selector(dummyClicked:) forControlEvents:UIControlEventTouchUpInside];
}

- (void) dummyClicked:(id)sender
{
    [_startAddress resignFirstResponder];
    [_endAddress resignFirstResponder];
}

- (void)keyboardWillHide:(NSNotification *)notif {  
    [dummy removeFromSuperview];
}

@end
