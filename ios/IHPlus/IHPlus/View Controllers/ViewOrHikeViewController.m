//
//  ViewOrHikeViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewOrHikeViewController.h"
#import "HikeDetailsViewController.h"

@implementation ViewOrHikeViewController

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
    self.navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque;
    self.navigationItem.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"titlebar_logo.png"]];
    [label setText:[hike description]];
    
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

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // pass off the  hike to the next view
    if ([[segue identifier] isEqualToString:@"HikeDetail"]){
        HikeDetailsViewController *nextVC = segue.destinationViewController;
        [nextVC setHike:hike];
    }
}

@end
