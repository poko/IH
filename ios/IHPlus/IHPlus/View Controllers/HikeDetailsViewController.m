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
#import "VistaCellNote.h"
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
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
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
    NSString *url = [NSString stringWithFormat:@"http://ecoarttech.net/ih_plus/scripts/getHikes.php?hike_id=%@", [hike hikeId]];
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
    ScenicVista *vista = [[hike vistas] objectAtIndex:indexPath.row];
//    NSLog(@"vista date: %@", vista.date);
//    NSLog(@"vista action type: %@", [vista getActionType]);
    VistaCell *cell;
    switch ([vista getActionType]){
        case PHOTO:{ 
            cell = (VistaCellPhoto *) [tableView dequeueReusableCellWithIdentifier:@"PhotoCell"];
//            NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://www.ecoarttech.org/ih_plus/uploads/%@", [vista photoUrl]]];
//            UIImage *img = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]]; 
//            [[(VistaCellPhoto *)cell photo] setImage:img];
            break;}
        case MEDITATE:{
             cell = (VistaCell *) [tableView dequeueReusableCellWithIdentifier:@"MeditateCell"];
            break;}
        default:{
            cell = (VistaCell *) [tableView dequeueReusableCellWithIdentifier:@"NoteCell"];
//            [[(VistaCellNote *) cell note] setText:[vista note]];
//            [[(VistaCellNote *) cell note] sizeToFit];
            break;}
    }
    // Configure the cell...
    [[cell vistaNum] setText:[NSString stringWithFormat:@"Scenic Vista %i", (indexPath.row + 1)]];
    [[cell prompt] setText:[vista prompt]];
    [[cell prompt] sizeToFit];
    // adjust things
    if ([vista getActionType] == PHOTO){
        //[cell insertSubview:[(VistaCellPhoto *) cell photo] belowSubview:[cell prompt]];
    }
    else if ([vista getActionType] == TEXT || [vista getActionType] == NOTE){
        [[(VistaCellNote *) cell note] setText:[vista note]];
        [[(VistaCellNote *) cell note] setBackgroundColor:[UIColor redColor]];
        CGRect frame= [[cell prompt] frame];
        CGRect noteFrame = CGRectMake(frame.origin.x, (frame.origin.y + frame.size.height), 300, 50);
        [[(VistaCellNote *) cell note] setFrame:noteFrame];
        [[(VistaCellNote *) cell note] sizeToFit];
    }
    // color bg
    UIColor *color = indexPath.row % 2 == 0 ?
    [UIColor colorWithRed:(222.0/255.0) green:(222.0/255.0) blue:(224.0/255.0) alpha:1]:
    [UIColor colorWithRed:(203.0/255.0) green:(204.0/255.0) blue:(208.0/255.0) alpha:1];
    [[cell contentView] setBackgroundColor:color];
    [cell sizeToFit];
    NSLog(@"returning cell: %i", indexPath.row);
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ScenicVista *vista = [[hike vistas] objectAtIndex:indexPath.row];
    NSLog(@"getting height %i", indexPath.row);
    if ([vista getActionType] == PHOTO){
        return 200;
    }
    return 150;
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
    NSArray *allHikes = [json objectForKey:@"hikes"];
    NSLog(@"all hikes count: %i", [allHikes count]);
    if ([allHikes count] == 1){ //only one person walked this hike before
        hike = [Hike initWithDictionary:[allHikes objectAtIndex:0]];
    }
    else{
        //TODO - what happens?!
    }
    // TODO update table view 
    [_table reloadData];
}

@end
