//
//  HikeDetailsViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "HikeDetailsViewController.h"
#import "VistaCell.h"
#import "VistaCellPhoto.h"
#import "ScenicVista.h"

@implementation HikeDetailsViewController
@synthesize hike;

NSMutableData *receivedData;

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


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    // load ui
    [_header setText:[hike name]];
    [_desc setText:[hike description]];
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MM.dd.yyyy"];
    NSString *details = [NSString stringWithFormat:@"Pioneered by: %@, %@", [hike username], [dateFormatter stringFromDate:[hike date]]];
    [_details setText:details];
    // make call to load the full hike data
    //loading dialog
    if (_loadingIndicator == nil){
        _loadingIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [_loadingIndicator setHidesWhenStopped:YES];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 60.0, 60.0);
        _loadingIndicator.center = self.view.center;
    }
    [self.view addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
    // make server call
    NSString *url = [NSString stringWithFormat:@"http://ecoarttech.net/ih_plus/scripts/getHike.php?hike_id=%@", [hike hikeId]];
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

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[hike vistas] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"cellforrowatIndex, section: %i , row: %i", indexPath.section, indexPath.row);
    ScenicVista *vista = [[hike vistas] objectAtIndex:indexPath.row];
    VistaCell *cell;
    if ([vista getActionType] == ActionType.PHOTO){
        *cell = (VistaCellPhoto *) [tableView dequeueReusableCellWithIdentifier:@"PhotoCell"];
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://www.ecoarttech.org/ih_plus/XXX/%@", [vista photoUrl]]];
        UIImage *image = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]]; 
        [[cell photo] setImage:Image];
    }
    else if ([vista getActionType] == ActionType.MEDITATE){
        *cell = (VistaCellMeditate *) [tableView dequeueReusableCellWithIdentifier:@"MeditateCell"];
    }
    else{ //Note/Text type
        *cell = (VistaCellNote *) [tableView dequeueReusableCellWithIdentifier:@"NoteCell"];
        [[cell note] setText:[vista response]];
    }
    // Configure the cell...
    [[cell vistaNum] setText:[NSString stringWithFormat:@"Scenic Vista @i", (indexPath.row + 1)]];
    [[cell prompt] setText:[vista prompt]];
    
    return cell;
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
    hike = [Hike initWithDictionary:[json objectForKey:@"hike"]];
    // TODO update table view 
    [_table reloadData];
}

@end
