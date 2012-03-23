//
//  SearchViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SearchViewController.h"
#import "SearchTableViewCell.h"
#import <MapKit/MapKit.h>
#import "Hike.h"
#import "ViewOrHikeViewController.h"



@implementation SearchViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
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

- (void)viewDidLoad
{
    [super viewDidLoad];
    _hikes = [[NSMutableArray alloc] init];
    [_searchBar setDelegate: self];
    [_tableView setDelegate: self];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque;
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    _hikes = nil;
    _searchBar = nil;
    _tableView = nil;
    _geocoder = nil;
    _loadingIndicator = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

//-(void)scrollViewDidScroll:(UIScrollView *)scrollView 
//{
//    NSLog(@"scroll");
//    CGRect rect = _searchBar.frame;
//    rect.origin.y = MIN(0, scrollView.contentOffset.y);
//    _searchBar.frame = rect;
//}

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // pass off the selected hike to the next view
    if ([[segue identifier] isEqualToString:@"ViewOrHike"]){
        ViewOrHikeViewController *nextVC = segue.destinationViewController;
        NSIndexPath *selected = [_tableView indexPathForSelectedRow];
        [nextVC setHike:[_hikes objectAtIndex:selected.row]];
    }
}

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_hikes count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    SearchTableViewCell *cell = (SearchTableViewCell *) [tableView dequeueReusableCellWithIdentifier:@"SearchItem"];
    Hike *hike =  (Hike *) [_hikes objectAtIndex:indexPath.row];
    // Configure the cell...
    [[cell name] setText:[hike name]];
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MM.dd.yyyy"];
    NSString *desc = [NSString stringWithFormat:@"%@, %@", [hike description], [dateFormatter stringFromDate:[hike date]]];
    [[cell description] setText:desc];
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
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
        [_loadingIndicator setHidesWhenStopped:YES];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 60.0, 60.0);
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
                NSString *url = [NSString stringWithFormat:@"http://ecoarttech.net/ih_plus/scripts/getHikesByLocation.php?latitude=%f&longitude=%f", latitude, longitude];
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
    }
    NSLog(@"Here is the escaped response: %@", json);
    // parse json into hike objects and update table
    NSArray *hikes = [json objectForKey:@"hikes"];
    if ([hikes count] == 0){
        //TODO show no results.
        return;
    }
    for (NSDictionary* hike in hikes) {
        Hike *hikeObj = [Hike initWithDictionary:hike];
        [_hikes addObject:hikeObj];
    }
    // update table view
    [_tableView reloadData];
}
@end
