//
//  MapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MapViewController.h"
#import "GetDirectionsDelegate.h"
#import "ScenicVista.h"

#define CURRENT_LOCATION @"Current Location"

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
    NSLog(@"MapViewController low mem");
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

-(void)viewDidAppear:(BOOL)animated
{
    NSLog(@"map did appear: %@", [self class]);
    if (_mapView != nil){
        [self.view insertSubview:_mapView belowSubview:_inputHolder];
        [_mapView setDelegate:self];
    }
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
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
}
 

-(void) mapView:(MKMapView *)mapView didAddOverlayViews:(NSArray *)overlayViews{
    NSLog(@"overlays added");
}

- (void) mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation{
    
    NSLog(@"User location : %@", userLocation.location );
    NSLog(@"how many regions we tracking? %i", [[_locMgr monitoredRegions] count]);
    _currentLocation = userLocation;
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
    NSLog(@" map view unloaded"); // TODO this may be removed when we stop debugging? (creating lots of monitoring regions)
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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
        [_loadingIndicator setBackgroundColor:[UIColor colorWithWhite:0 alpha:.5]];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 320.0, 480.0);
        _loadingIndicator.center = self.view.center;
    }
    [self.view addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
}

bool alertShowing = false;
-(void)hideLoadingDialog:(NSString *) error
{
    [_loadingIndicator stopAnimating];
    if (error != nil && !alertShowing){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Creating Hike" message:[NSString stringWithFormat:@"There was an error creating the hike: %@", error]
             delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        alertShowing = true;
    }
}
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    NSLog(@"dismissed with index: %i", buttonIndex);
    alertShowing = false;
}

-(void) prepareNewHike
{
    NSLog(@"wrong prepareNewHike method");
    assert(false);
}

-(void) pathGenerated: (int) midpoint
{
    NSLog(@"wrong pathGenerated method");
    assert(false);
}

-(float) getRandomOffset
{
    float min = .0005f;
    float max = .005f;
    //double num = Math.random() * (max - min);
    float diff = max - min;
    float offset =  (((float) (arc4random() % ((unsigned)RAND_MAX + 1)) / RAND_MAX) * diff) + min;
    //int i = (offset / .0001) % 2 == 0 ? 1 : -1;
    int i = 1;
    int temp = offset / .0001;
    if (temp % 2 != 0)
        i = -1;
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
        //[_pathPoints addObjectsFromArray:points];
        NSLog(@"compeltion handler!! %i", _callCount);
//        if (_callCount == 1){
//            midpoint = ([_pathPoints count] - 1);
//            [self getDirectionsFrom:to to:[_endAddress text]];
//        }
//        if (_callCount == 2){
            NSLog(@"should be drawing now, %i", [points count]);
        NSLog(@"who am I: %@, and who is my delegate? %@", [self class], [_mapView delegate]);
            // Create the Hike object!
            _hike = [[Hike alloc] init];
            [_hike setOriginal:@"true"];
            // add points to it
            [_hike setPoints:points];
            
            // hide the input view
            [_inputHolder setHidden:YES];
            UIBarButtonItem *createButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(clickedCreateButton:)];
            [[self navigationItem] setLeftBarButtonItem:createButton];
        
        // draw path overlay
        CLLocationCoordinate2D *pointsLine = malloc([[_hike points] count] * sizeof(CLLocationCoordinate2D));
        for (int i = 0; i < [[_hike points] count]; i++){
            pointsLine[i] = [(CLLocation *)[[_hike points] objectAtIndex:i ] coordinate];
        }
        MKPolyline *line = [MKPolyline polylineWithCoordinates:pointsLine count:[[_hike points] count]];
        _routeLine = line;
        MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance([[[_hike points] objectAtIndex:0 ] coordinate], 400, 400);
        [_mapView setRegion:region animated:YES];
        [_mapView addOverlay:line];
        free(pointsLine);
        // Have subclasses do whatever work they need .. 
        [self pathGenerated:midpoint];
                            
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
    [_inputHolder setHidden:false];
    [[self navigationItem] setLeftBarButtonItem:nil];
}

#pragma mark IBActions
-(IBAction)hitTrail:(id)sender
{
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
    // have subclasses do their prep (remove any pending proximity alerts, etc)
    [self prepareNewHike];
    
    // check that both fields have values
    NSString *start = [_startAddress text];
    NSString *end = [_endAddress text];
    if ([start length] > 0 && [end length] > 0){
        // start making calls!
        // fwd geocode start location
        [self showLoadingDialog];
        //check if "current location"
        if ([start isEqualToString:CURRENT_LOCATION]){
            //TODO - if currentLocation is empty .. ? 
            // get random offset from current location
            float randLat = [self getRandomOffset]+_currentLocation.location.coordinate.latitude;
            float randLng = [self getRandomOffset]+_currentLocation.location.coordinate.longitude;
            NSString *startPoints = [NSString stringWithFormat:@"%f,%f",
                                     _currentLocation.location.coordinate.latitude, 
                                     _currentLocation.location.coordinate.longitude];
            NSString *randPoint = [NSString stringWithFormat:@"%f,%f",randLat, randLng];
            [self getDirectionsFrom:startPoints to:randPoint];
            [self getDirectionsFrom:randPoint to:end];
        }
        else{
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
}


-(IBAction)currentLocation:(id)sender
{
    NSLog(@"current loc");
    [_startAddress setText:CURRENT_LOCATION];
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
                [imagePicker setAllowsEditing:false];
                
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
    return false;
}

#pragma mark geofencing
- (void)showActionView
{   // show action view
    [_promptHolder setHidden:false];
    [_prompt setText:[_currentVista prompt]];
}

- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error
{
    NSLog(@"failed to monitor region!!, %@", error);
}

- (void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    NSLog(@"entered region! %@", [region identifier]);
    _currentVista = [_hike getVistaById:[region identifier]];
    [self showActionView];
}

- (void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region
{
    NSLog(@"exited region!");
}

-(void)locationManager:(CLLocationManager *)manager didStartMonitoringForRegion:(CLRegion *)region
{
    NSLog(@"monitoring region: %@", [region identifier]);
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
