//
//  MapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MapViewController.h"

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


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
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

#pragma mark IBActions
-(IBAction)hitTrail:(id)sender
{
    NSLog(@"hit trail");
    // check that both fields have values
    NSString *start = [_startAddress text];
    NSString *end = [_endAddress text];
    if ([start length] > 0 && [end length] > 0){
        // start making calls!
        // fwd geocode start location
        [self showLoadingDialog];
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
                 float randLat = [self getRandomOffset];
                 float randLng = [self getRandomOffset];
                 NSLog(@" lat: %f", latitude);
                 NSLog(@" lng: %f", longitude);
                 NSLog(@"Rand lat: %f", (randLat+latitude));
                 NSLog(@"Rand lng: %f", (randLng+longitude));
                 // make directions calls
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
                 NSLog(@"Nothing returned for search");
                 // TODO
//                 [_loadingIndicator stopAnimating];
//                 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Invalid Location" message:[NSString stringWithFormat:@"Sorry, %@ isn't a location we can find", [searchBar text]] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
//                 [alert show];
             } 
         }];

        // make directions calls to start->random and random->start
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
    else if (textField == _endAddress)
        NSLog(@"end go");
    return NO;
}

@end
