//
//  TestMapVC.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/27/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "SearchViewController.h"
#import <MapKit/MapKit.h>
#import "AppDelegate.h"
#import "Hike.h"
#import "SearchResultsController.h"
#import "Constants.h"


@implementation SearchViewController

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

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSLog(@"preparing for segue: %@", segue.identifier);
    if ([segue.identifier isEqualToString:@"SearchResults"]){
        SearchResultsController *resultsVC = [segue destinationViewController];
        [resultsVC setHikes:_hikes];
        [resultsVC setSearchTerm:[_searchBar text]];
        self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"search" style:UIBarButtonItemStylePlain target:nil action:nil];
    }
    else{
        // credits
    }
}

#pragma mark - View lifecycle

-(void) viewWillAppear:(BOOL)animated
{
    NSLog(@"search viewWillAppear.");
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Archives Mode" message:@"You have selected the option to view past documentation from other people's hikes."
                                                       delegate:self cancelButtonTitle:@"cancel" otherButtonTitles:@"continue",nil];
    [alert show];
}

-(void)viewDidAppear:(BOOL)animated
{
    MKMapView *map = [(AppDelegate *)[[UIApplication sharedApplication] delegate] map];
    [self.view insertSubview:map belowSubview:_searchBar];
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    NSLog(@"hello map test!");
    _hikes = [[NSMutableArray alloc] init];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque; 
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    _searchBar = nil;
    _geocoder = nil;
    _loadingIndicator = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


#pragma mark - Alert View Delegate
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 0){ //canceled
        [self.tabBarController setSelectedIndex:0];
    }
    else if (buttonIndex == 1){ //continue
        //do nothing
    }
}


#pragma mark - Search bar delegate
NSMutableData *receivedData;
-(void) searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
    NSLog(@"oo, we searching %@", searchBar.text);
    // remove any current hikes from data
    [_hikes removeAllObjects];
    [searchBar resignFirstResponder];
    //loading dialog
    if (_loadingIndicator == nil){
        _loadingIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [_loadingIndicator setBackgroundColor:[UIColor colorWithWhite:0 alpha:.5]];
        [_loadingIndicator setHidesWhenStopped:YES];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 320.0, [[UIScreen mainScreen] bounds].size.height );
        _loadingIndicator.center = self.view.center;
    }
    [self.view addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
    // geocode search
    if (!_geocoder){
        _geocoder = [[CLGeocoder alloc] init];
    }
    [_geocoder geocodeAddressString:[searchBar text] completionHandler:
     ^(NSArray* placemarks, NSError* error){
         if ([placemarks count] > 0){
             CLPlacemark *placemark = [placemarks objectAtIndex:0];
             CLLocationDegrees latitude = placemark.location.coordinate.latitude;
             CLLocationDegrees longitude = placemark.location.coordinate.longitude;
             // make server call
            NSString *url = [NSString stringWithFormat:@"%@getHikesByLocation.php?latitude=%f&longitude=%f", BASE_URL, latitude, longitude];

             NSLog(@"Sending to url %@", url);
             
             
             NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];    
             NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:self];
             if (connection) {
                 // Create the NSMutableData to hold the received data.
                 // receivedData is an instance variable declared elsewhere.
                 receivedData = [NSMutableData data];
             } else {
                 NSLog(@"connection failed");
                 [_loadingIndicator stopAnimating];
                 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
                 [alert show];
             }
         }
         else{
             NSLog(@"Nothing returned for search");
             [_loadingIndicator stopAnimating];
             UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Invalid Location" message:[NSString stringWithFormat:@"Sorry, %@ isn't a location we can find", [searchBar text]] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
             [alert show];
         } 
     }];
}

- (void)searchBarCancelButtonClicked:(UISearchBar *) searchBar{
    [searchBar resignFirstResponder];
}

#pragma mark - Connection delgate

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    // This method is called when the server has determined that it
    // has enough information to create the NSURLResponse.
    
    // It can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
    
    // receivedData is an instance variable declared elsewhere.
    NSLog(@"didReceiveResponse");
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
    [_loadingIndicator stopAnimating];
    NSLog(@"Connection failed! Error - %@ %@", [error localizedDescription], [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Network Error" message:@"There was an error connecting to the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // do something with the data
    // receivedData is declared as a method instance elsewhere
    [_loadingIndicator stopAnimating];
    NSLog(@"Succeeded! Received %d bytes of data",[receivedData length]);
    NSString *receivedStr = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
    NSString *escaped = [receivedStr stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    NSError *error;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[escaped dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:&error];
    if (error != nil){
        NSLog(@"Here is the error: %@", error);
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Server Error" message:@"There was an error with the server." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    NSLog(@"Here is the escaped response: %@", json);
    // parse json into hike objects and update table
    NSArray *hikes = [json objectForKey:@"hikes"];
    if ([hikes count] == 0){
        //no results.
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No Hikes" message:
                              [NSString stringWithFormat:@"No hikes found near %@.", [_searchBar text]] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    for (NSDictionary* hike in hikes) {
        Hike *hikeObj = [Hike initWithDictionary:hike];
        [_hikes addObject:hikeObj];
    }
    
    [self performSegueWithIdentifier:@"SearchResults" sender:self];
}

@end
