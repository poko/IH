//
//  HikeDetailsViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "HikeDetailsViewController.h"
#import "SearchTableViewCell.h"

@implementation HikeDetailsViewController
@synthesize hike;

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
    SearchTableViewCell *cell = (SearchTableViewCell *) [tableView dequeueReusableCellWithIdentifier:@"PhotoCell"];
    // Configure the cell...
    [[cell name] setText:[hike name]];
    //[[cell description] setText:desc];
    
    return cell;
}

@end
