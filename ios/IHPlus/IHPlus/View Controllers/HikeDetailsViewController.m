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
#import "ImageLoader.h"
#import "Constants.h"

#define FONT_SIZE 14.0f
#define CELL_CONTENT_WIDTH 320.0f
#define CELL_CONTENT_MARGIN 10.0f

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

- (void) loadUI
{
    NSLog(@"hike: %@", hike);
    NSLog(@"hike name? %@ ", hike.name);
    [_header setText:[hike name]];
    [_desc setText:[hike description]];
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MM.dd.yyyy"];
    NSString *details = [NSString stringWithFormat:@"Pioneered by: %@, %@", [hike username], [dateFormatter stringFromDate:[hike date]]];
    [_details setText:details];
}


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    [pageControl setHidden:true];
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
    // load ui
    [self loadUI];
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
    NSString *url = [NSString stringWithFormat:@"%@getHikes.php?hike_id=%@", BASE_URL, [hike hikeId]];
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

#pragma mark - page control
- (IBAction)changePage:(id)sender {
    int page = pageControl.currentPage;
    hike = [Hike initWithDictionary:[_hikes objectAtIndex:page]];
    [self loadUI];
    [_table reloadData];
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
    VistaCell *cell;
    switch ([vista getActionType]){
        case PHOTO:{ 
            cell = (VistaCellPhoto *) [tableView dequeueReusableCellWithIdentifier:@"PhotoCell"];
            break;}
        case MEDITATE:{
             cell = (VistaCell *) [tableView dequeueReusableCellWithIdentifier:@"MeditateCell"];
            break;}
        default:{
            cell = (VistaCell *) [tableView dequeueReusableCellWithIdentifier:@"NoteCell"];
            break;}
    }
    // Configure the cell...
    [[cell vistaNum] setText:[NSString stringWithFormat:@"Scenic Vista %i", (indexPath.row + 1)]];
    [[cell prompt] setText:[vista prompt]];
    [[cell prompt] sizeToFit];
    // adjust things
    if ([vista getActionType] == PHOTO){
        NSString *urlString = [NSString stringWithFormat:@"%@%@", BASE_PHOTO_URL, [vista photoUrl]]; 
        UIImage *img = [ImageLoader getImageForUrl:urlString];
        if (img != nil){
            float imgHeight = (img.size.height * 300)/img.size.width;
            CGRect frame = [[cell prompt] frame];
            CGRect photoFrame = CGRectMake(frame.origin.x, (frame.origin.y + frame.size.height + 10), 300, imgHeight);
            [[(VistaCellPhoto *)cell photo] setFrame:photoFrame];
            [[(VistaCellPhoto *)cell photo] setImage:img];
        }
    }
    else if ([vista getActionType] == TEXT || [vista getActionType] == NOTE){
        [[(VistaCellNote *) cell note] setText:[vista note]];
        CGRect frame = [[cell prompt] frame];
        CGRect noteFrame = CGRectMake(frame.origin.x, (frame.origin.y + frame.size.height + 10), 300, 50);
        [[(VistaCellNote *) cell note] setFrame:noteFrame];
        [[(VistaCellNote *) cell note] sizeToFit];
    }
    // color bg
    UIColor *color = indexPath.row % 2 == 0 ?
    [UIColor colorWithRed:(222.0/255.0) green:(222.0/255.0) blue:(224.0/255.0) alpha:1]:
    [UIColor colorWithRed:(203.0/255.0) green:(204.0/255.0) blue:(208.0/255.0) alpha:1];
    [[cell contentView] setBackgroundColor:color];
    [cell sizeToFit];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ScenicVista *vista = [[hike vistas] objectAtIndex:indexPath.row];
    NSLog(@"getting height %i", indexPath.row);
    NSString *text = [NSString stringWithFormat:@"%@ \n %@", [vista prompt], [vista note]];
    
    CGSize constraint = CGSizeMake(CELL_CONTENT_WIDTH - (CELL_CONTENT_MARGIN * 2), 20000.0f);
    
    CGSize size = [text sizeWithFont:[UIFont systemFontOfSize:FONT_SIZE] constrainedToSize:constraint lineBreakMode:UILineBreakModeWordWrap];
    
    CGFloat height = MAX(size.height, 44.0f) + 20;
    
    if ([vista getActionType] == PHOTO){
        NSString *urlString = [NSString stringWithFormat:@"%@%@", BASE_PHOTO_URL, [vista photoUrl]];
        UIImage *img = [ImageLoader getImageForUrl:urlString];
        float imgHeight = 0;
        if (img != nil){
            imgHeight = (img.size.height * 300)/img.size.width;
        }
        return height + (CELL_CONTENT_MARGIN * 2) + imgHeight;
    }
    return height + (CELL_CONTENT_MARGIN * 2);
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
    _hikes = [json objectForKey:@"hikes"]; //TODO save allHikes so we can shuffle through
    NSLog(@"all hikes count: %i", [_hikes count]);
    hike = [Hike initWithDictionary:[_hikes objectAtIndex:0]];
    if ([_hikes count] > 1){ //multiple people walked this hike before
        // TODO show page controller
        [pageControl setHidden:false];
        [pageControl setNumberOfPages:[_hikes count]];
        [_table setFrame:CGRectMake(0, 111, 320, 256)]; // move table below page controller
    }
    else{
        [pageControl setHidden:true];
        [_table setFrame:CGRectMake(0, 75, 320, 292)]; // move table to below header labels
    }
    //update table view 
    [_table reloadData];
}

@end
