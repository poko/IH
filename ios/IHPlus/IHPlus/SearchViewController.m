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
   // [[self navigationController] setna
    //[self.navigationController setNavigationBarStyle: UIBarStyleBlackTranslucent]];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque;
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // TODOs
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
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

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

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
    [searchBar resignFirstResponder];
    // TODO popup loading dialog
    // reverse geocode the address (?)
    //geocoder
    if (!_geocoder){
        _geocoder = [[CLGeocoder alloc] init];
    }
    [_geocoder geocodeAddressString:[searchBar text] completionHandler:
        ^(NSArray* placemarks, NSError* error){
            if ([placemarks count] > 0){
                CLPlacemark *placemark = [placemarks objectAtIndex:0];
                CLLocationDegrees latitude = placemark.location.coordinate.latitude;
                CLLocationDegrees longitude = placemark.location.coordinate.longitude;
                //TODO - loading dialog
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
                    // TODO Inform the user that the connection failed.
                    NSLog(@"connection failed");
                }
            }
            else{
                //TODO ERROR
                NSLog(@"Nothing returned for search");
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
    NSLog(@"Connection failed! Error - %@ %@", [error localizedDescription], [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // do something with the data
    // receivedData is declared as a method instance elsewhere
    NSLog(@"Succeeded! Received %d bytes of data",[receivedData length]);
    NSString *receivedStr = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
    NSString *escaped = [receivedStr stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    NSError *error;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[escaped dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:&error];
    if (error != nil)
        NSLog(@"Here is the error: %@", error); //TODO error
    NSLog(@"Here is the escaped response: %@", json);
    // parse json into hike objects and update table
    NSArray *hikes = [json objectForKey:@"hikes"];
    if ([hikes count] == 0){
        //TODO show no results.
        return;
    }
    for (NSDictionary* hike in hikes) {
        NSLog(@"Here is a hike: %@", hike);
        Hike *hikeObj = [Hike initWithDictionary:hike];
        [_hikes addObject:hikeObj];
    }
    // update table view
    [_tableView reloadData];
}
@end
