//
//  MapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 ecoarttech_. All rights reserved.
//

#import "MapViewController.h"
#import "GetDirectionsDelegate.h"
#import "ScenicVista.h"
#import "Toast+UIView.h"
#import "Constants.h"
#import "AppDelegate.h"
#import "AssetsLibrary/AssetsLibrary.h"

#define CURRENT_LOCATION @"Current Location"
#define CREATE_ERROR_ALERT 10
#define NEW_HIKE_ALERT 20

@implementation MapViewController

BOOL uploadToastShown = false;

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
    //NSLog(@"MapViewController low mem");
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

-(void)viewDidAppear:(BOOL)animated
{
    //NSLog(@"map did appear: %@", [self class]);
    if (_mapView != nil){
        [self.view insertSubview:_mapView belowSubview:_inputHolder];
        [_mapView setDelegate:self];
    }
    
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    NSLog(@"map view did loaded");
    [_endAddress setDelegate:self]; //[_endAddress setText:@"1300 bob harrison 78702"];//TODOx
    [_startAddress setDelegate:self]; //[_startAddress setText:@"1200 bob harrison austin, tx"];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque; 
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
    [_inputHolder setBackgroundColor:[[UIColor alloc] initWithPatternImage:[UIImage imageNamed:@"black_gradient.png"]]];
    _zoomed = false;
    // keyboard handling
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) 
                                                 name:UIKeyboardWillShowNotification object:self.view.window]; 
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) 
                                                 name:UIKeyboardWillHideNotification object:self.view.window]; 
    NSLog(@"adding controller as observer of currentHike.");
    AppDelegate *delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [delegate addObserver:self forKeyPath:@"hike" options:NSKeyValueObservingOptionOld context:nil];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    NSLog(@"map view unloaded");
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
 
#pragma mark - MapView Delegate

- (void) mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation{
    
    //NSLog(@"User location : %@", userLocation.location );
    if (userLocation != nil)
        _currentLocation = userLocation;
    if (!_zoomed){
        MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance([userLocation coordinate], 400, 400);
        [_mapView setRegion:region animated:YES];
        _zoomed = true;
    }
    // check if we've entered a vista point
    if (![_hike companion] && (_currentVista == nil)){
    for (ScenicVista *vista in [_hike vistas]){
        if (![vista complete]){ //only care about not completed vistas.
            // check if entered region
//            NSLog(@"lat diff: %f", fabs(vista.location.coordinate.latitude - userLocation.coordinate.latitude));
//            NSLog(@"lng diff: %f", fabs(vista.location.coordinate.longitude - userLocation.coordinate.longitude));
            if (fabs(vista.location.coordinate.latitude - userLocation.coordinate.latitude) <= .0001 && fabs(vista.location.coordinate.longitude - userLocation.coordinate.longitude) <= .0001){
                // we're here!
                NSLog(@"entered region!");
                _currentVista = vista;
                [self showActionView];
                return;
            }
        }
    }
    }
    
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id )overlay
{
    MKOverlayView* overlayView = nil;
    
    if(overlay == _routeLine)
    {
        //NSLog(@"trying to add route line");
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

- (MKAnnotationView *)mapView:(MKMapView *)map viewForAnnotation:(id <MKAnnotation>)annotation
{
    if ([annotation isKindOfClass:[MKUserLocation class]])
        return nil;
    static NSString *AnnotationViewID = @"annotationViewID";
    NSLog(@"View for annotation: %@", [annotation title]);
    MKAnnotationView *annotationView = (MKAnnotationView *)[map dequeueReusableAnnotationViewWithIdentifier:AnnotationViewID];
    
    if (annotationView == nil)
    {
        annotationView = [[MKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:AnnotationViewID];
    }
    ScenicVista *vista = [_hike getVistaById:[annotation title]];
    NSString *imgName = [vista complete] ? @"visited_vista.png" :@"scenic_vista_point.png";
    annotationView.image = [UIImage imageNamed:imgName];
    annotationView.annotation = annotation;
    
    return annotationView;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

NSTimer *_timer;
-(void)showLoadingDialog
{
    SEL selector = @selector(hideLoadingDialog:);
    
    NSMethodSignature *signature = [MapViewController instanceMethodSignatureForSelector:selector];
    NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
    [invocation setSelector:selector];
    
    NSString *str1 = @"Timeout";
    
    //Set the arguments
    [invocation setTarget:self];
    [invocation setArgument:&str1 atIndex:2];
    
    // start failure timer to remove dialog incase something goes awry
    _timer = [NSTimer scheduledTimerWithTimeInterval:10 invocation:invocation repeats:NO];
    //loading dialog
    if (_loadingIndicator == nil){
        _loadingIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [_loadingIndicator setHidesWhenStopped:YES];
        [_loadingIndicator setBackgroundColor:[UIColor colorWithWhite:0 alpha:.5]];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 320.0, [[UIScreen mainScreen] bounds].size.height );
        _loadingIndicator.center = self.view.center;
    }
    [self.view addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
}

bool alertShowing = false;
-(void)hideLoadingDialog:(NSString *) error
{
    [_loadingIndicator stopAnimating];
    // stop timer
    [_timer invalidate];
    if (error != nil && !alertShowing){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Creating Hike" message:[NSString stringWithFormat:@"There was an error creating the hike: %@", error]
             delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        alertShowing = true;
    }
    
}
//- (void) showFailureAlert
//{
//    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Uh Oh" 
//                                                    message:@"Something went wrong!" 
//                                                   delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Ok", nil];
//    [alert show];
//}

- (void) newHike: (BOOL) clearMapView
{
    //clear hike
    NSLog(@"clearing hike");
    _hike = nil;
    uploadToastShown = false;
    if (clearMapView)
        [self removeOverlaysAndAnnotations];
    [_promptHolder setHidden:true];
    [_inputHolder setHidden:false];
    [[self navigationItem] setLeftBarButtonItem:nil];
    if ([[[self navigationItem] rightBarButtonItem] isEqual:_uploadButton]){
        UIBarButtonItem *button = [[UIBarButtonItem alloc] initWithCustomView:_infoButton];    
        [[self navigationItem] setRightBarButtonItem:button];
    }
}



#pragma mark - Alert View Delegate
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    //NSLog(@"MapView dismissed with index: %i", buttonIndex);
    if ([alertView tag] == CREATE_ERROR_ALERT){
        alertShowing = false;
    }
    else if ([alertView tag] == NEW_HIKE_ALERT){
        // check if user canceled or clicked 'yes'
        if (buttonIndex == 1){ //clicked 'yes'
            [self newHike:true];
        }
    }
}

-(void) pathGenerated: (int) midpoint
{
    NSLog(@"wrong pathGenerated method");
    assert(false);
}


- (void)drawPath
{
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
}

-(float) getRandomOffset
{
    float min = .0005f;
    float max = .005f;
    float diff = max - min;
    float offset =  (((float) (arc4random() % ((unsigned)RAND_MAX + 1)) / RAND_MAX) * diff) + min;
    int i = 1;
    int temp = offset / .0001;
    if (temp % 2 != 0)
        i = -1;
    return offset * i;
}



- (NSArray *) useLocalActions:(NSString *) plist
{
    //NSLog(@"USE LOCAL ACTIONS");
    // Path to the plist (in the application bundle)
    NSString *path = [[NSBundle mainBundle] pathForResource:plist ofType:@"plist"];        
    // Build from the plist  
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc]initWithContentsOfFile:path];
    // Show the string values
    NSMutableArray *localActions = [NSMutableArray array];
    for (NSString *indx in [dictionary allKeys]){
        NSArray *values = [dictionary objectForKey:indx];
        NSDictionary *action = [[NSDictionary alloc] init];
        [action setValue:indx forKey:KEY_ACTION_ID];
        [action setValue:[values objectAtIndex:0] forKey:KEY_ACTION_TYPE];
        [action setValue:[values objectAtIndex:1] forKey:KEY_ACTION_PROMPT];
        [localActions addObject:action];
    }
    return localActions;
}

int midpoint;

-(void) getDirectionsFrom:(NSString *) from to:(NSString *) to
{
    //NSLog(@"getting directions from : %@ to: %@", from, to);
    NSString *url = [NSString stringWithFormat:@"http://maps.googleapis.com/maps/api/directions/json?sensor=false&origin=%@&destination=%@", 
                     [from stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding], 
                     [to stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    NSLog(@"Sending to url %@", url);
                     
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
    GetDirectionsDelegate *connDelegate = [[GetDirectionsDelegate alloc] initWithHandler:^(NSMutableArray *points, NSString *error) {
        //NSLog(@"handler gets: %i", [points count]);
        if (error != nil){
            [self hideLoadingDialog:error];
            return;
        }
        _callCount++;
        [_pathPoints addObjectsFromArray:points];
        //NSLog(@"compeltion handler!! %i", _callCount);
        if (_callCount == 1){
            midpoint = ([_pathPoints count] - 1);//set midpoint as last index from first call
            //[self getDirectionsFrom:to to:[_endAddress text]];
        }
        if (_callCount == 2){
            //NSLog(@"should be drawing now, %i", [points count]);
            // Create the Hike object!
            _hike = [[Hike alloc] init]; 
            [_hike setOriginal:@"true"];
            CLLocation *start = (CLLocation *)[_pathPoints objectAtIndex:0];
            [_hike setStartLat:[NSString stringWithFormat:@"%f", start.coordinate.latitude]];
            [_hike setStartLng:[NSString stringWithFormat:@"%f", start.coordinate.longitude]];
            // add all points to it
            [_hike setPoints:_pathPoints];
            _pathPoints = nil;
            
            // hide the input view
            [_inputHolder setHidden:YES];
            // create a 'back' button to get input view back
            UIBarButtonItem *backButton = [[UIBarButtonItem alloc] 
                                       initWithTitle:@"Edit" 
                                       style:UIBarButtonItemStyleBordered
                                       target:self action: @selector(clickedCreateButton:)];
            [[self navigationItem] setLeftBarButtonItem:backButton];
        
        [self drawPath];
        // Have subclasses do whatever work they need .. 
        [self pathGenerated:midpoint];
       }
        
    }];
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:connDelegate];
    if (!connection) {
        //NSLog(@"connection failed");
        [self hideLoadingDialog:@"Could not connect to server"];
    }
}

-(void) completeCurrentVista
{
    NSLog(@"completing current vista. Total vistas: %i", [[_hike vistas] count]);
    [_currentVista setComplete:YES];
	NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    [_currentVista setDate:[dateFormatter stringFromDate:[NSDate date]]];
    // set vista annotation point image
    for (id annotation in _mapView.annotations){
        if ([[_currentVista actionId] isEqual:[annotation title]]){
            NSLog(@"removing and readding annotation: %@", [annotation title]);
            [_mapView removeAnnotation:annotation];
            [_mapView addAnnotation:annotation];
        }
    }
    _currentVista = nil;
    [_promptHolder setHidden:YES];
    // check if we can enable upload button
    if ([_hike eligibleForUpload]){
        _uploadButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(uploadHike:)];
        [[self navigationItem] setRightBarButtonItem:_uploadButton];
        if ([_hike companion] && !uploadToastShown){
            // show upload toast
            [self.view makeToast:@"You can now upload your hike using the upload button on the navigation bar."];
            uploadToastShown = true;
        }
    }
    // if all the vistas are complete, show the modal automagically. 
    if ([_hike isComplete]){
        NSLog(@"should perform seqgue??");
        [self performSegueWithIdentifier:@"UploadHike" sender:self];
    }
}

// called when user hits the 'back/create/edit' hike button after generating a hike
- (IBAction) clickedCreateButton:(id)sender{
    //NSLog(@"clicked create!");
    //check that user hasn't completed any vistas, if they have, warn.
    if ([_hike hasCompletedVista]){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"New Hike" 
                                                        message:@"You've already completed a vista!\nAre you sure you want to create a new hike? This hike will be lost." 
                                                       delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Yes", nil];
        [alert setTag:NEW_HIKE_ALERT];
        [alert show];
        return;
    }
    else{ // just show the input fields
        [self newHike:true];
    }
}

-(void) removeOverlaysAndAnnotations
{
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

}

#pragma mark IBActions
-(IBAction)hitTrail:(id)sender
{
    [_startAddress resignFirstResponder];
    [_endAddress resignFirstResponder];
    [self removeOverlaysAndAnnotations];
    // clear out any previous data
    _callCount = 0;
    _pathPoints = [[NSMutableArray alloc] init];
    
    // check that both fields have values
    NSString *start = [_startAddress text];
    NSString *end = [_endAddress text];
    if ([start length] > 0 && [end length] > 0){
        // start making calls!
        // fwd geocode start location
        [self showLoadingDialog];
        //check if "current location"
        if ([start isEqualToString:CURRENT_LOCATION]){
            // warn if we haven't gotten a current location
            if (_currentLocation == nil){
                [self.view makeToast:@"Unable to determine your current location."];
                [self hideLoadingDialog:nil];
                return;
            }
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
                 NSString *randPoint = [NSString stringWithFormat:@"%f,%f",randLat, randLng]; 
                 [self getDirectionsFrom:start to:randPoint];
                 [self getDirectionsFrom:randPoint to:end];
             }
             else{
                 //NSLog(@"Nothing returned for placemarks search");
                 [self hideLoadingDialog:@"Not a valid starting location"];
             } 
         }];
        }

    }
}


-(IBAction)currentLocation:(id)sender
{
    //NSLog(@"current loc");
    [_startAddress setText:CURRENT_LOCATION];
}

-(IBAction)continueClicked:(id)sender
{
    if (_currentVista != nil){
        switch ([_currentVista getActionType]){
            case MEDITATE:{
                //NSLog(@"MEDITATE!");
                [self completeCurrentVista];
                break;
            }
            case NOTE: {
                //NSLog(@"NOTE!");
                // popup modal for note taking
                [self performSegueWithIdentifier:@"NoteModal" sender:self];
                break;
            }
            case TEXT: {
                //NSLog(@"TEXT!");
                // popup modal for texting logic
                [self performSegueWithIdentifier:@"TextModal" sender:self];
                break;
            }
            case PHOTO: {
                //NSLog(@"PHOTO!");
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
            default:{ // This should ever happen, but ?? 
                _currentVista = nil;
                [_promptHolder setHidden:YES];
            }
        }
    }
    else{ //how did this happen?
        _currentVista = nil;
        [_promptHolder setHidden:YES];
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
    //NSLog(@"called uploadModalController done");
    [controller dismissViewControllerAnimated:YES completion:^{
        //NSLog(@"and hike should have uploaded! %@", error);
        if (error != nil){
            // boo!
            //NSLog(@" boooo error back in mapview");
            // show error toast (maybe user canceled, if we allow that option?)
            [self.view makeToast:@"Hike was not uploaded to server."];
        }
        else{
            // success!
            //NSLog(@" no error back in mapview");
            // show success "toast"
            [self.view makeToast:@"Hike uploaded successfully."];
            // clean up current Hike object, clear overlays, show input fields again 
            [self newHike:true];
        }
    }];
}

-(void) cancelUploadModalController:(UploadHikeController *)controller
{
    [controller dismissModalViewControllerAnimated:true];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{ 
    NSLog(@"preparing for segue: %@", segue.identifier);
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
    NSLog(@"finished taking photo");
    //dismiss picker
    [picker dismissModalViewControllerAnimated:true];
    // Access the uncropped image from info dictionary
    [self showLoadingDialog];
    UIImage *image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
    // Save image
    UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo
{
    UIAlertView *alert;
    // Unable to save the image  
    if (error){
        alert = [[UIAlertView alloc] initWithTitle:@"Error" 
                                           message:@"Unable to save image to Photo Album." 
                                          delegate:self cancelButtonTitle:@"Ok" 
                                 otherButtonTitles:nil];
    }
    else{ // All is well
        CGSize newImageSize = CGSizeMake(360, 480);
        UIImage *scaled = [MapViewController imageWithImage:image scaledToSize:newImageSize];
        NSString  *jpgPath = [NSHomeDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"Documents/%@.jpg", [_currentVista getUploadFileName]]];
        [UIImageJPEGRepresentation(scaled, 1.0) writeToFile:jpgPath atomically:YES]; 
        [self completeCurrentVista];
    }
    [self hideLoadingDialog:nil];
}


+ (UIImage*)imageWithImage:(UIImage*)image scaledToSize:(CGSize)newSize;
{
    // Create a graphics image context
    UIGraphicsBeginImageContext(newSize);
    
    // Tell the old image to draw in this new context, with the desired
    // new size
    [image drawInRect:CGRectMake(0,0,newSize.width,newSize.height)];
    
    // Get the new image from the context
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    // End the context
    UIGraphicsEndImageContext();
    
    // Return the new image.
    return newImage;
}


#pragma mark TextField Delegate

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == _startAddress){
        //NSLog(@"start next");
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
        //NSLog(@"end go");
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
