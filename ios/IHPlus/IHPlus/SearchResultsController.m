//
//  SearchViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SearchResultsController.h"
#import "SearchTableViewCell.h"
#import <MapKit/MapKit.h>
#import "Hike.h"
#import "ViewOrHikeViewController.h"



@implementation SearchResultsController

@synthesize hikes;

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
    //_hikes = [[NSMutableArray alloc] init];// TODO -not this
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
    _tableView = nil;
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
        [nextVC setHike:[hikes objectAtIndex:selected.row]];
    }
}

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [hikes count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    SearchTableViewCell *cell = (SearchTableViewCell *) [tableView dequeueReusableCellWithIdentifier:@"SearchItem"];
    Hike *hike =  (Hike *) [hikes objectAtIndex:indexPath.row];
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

@end
