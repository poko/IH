//
//  CompanionMapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/23/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "CompanionMapViewController.h"

@implementation CompanionMapViewController

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

#pragma mark - IBActions

-(IBAction)addVistaHere:(id)sender
{ 
    // drop pin on map at current location
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = _currentLocation.location.coordinate;
    [_mapView addAnnotation:annotationPoint];
    // add vista object to hike
    ScenicVista *vista = [[ScenicVista alloc] init];
    // TODO we will have a source of vista actions that we get from the server .. probably when we hit the trail
    [vista setLocation:_currentLocation.location];
    [vista setActionId:@""]; //TODO
    [vista setActionType:@""]; //TODO
    [vista setPrompt:@""]; //TODO
    [_hike addCompanionVista:vista];
    
    //show vista input
    _currentVista = vista;
    [self showActionView];
}

@end
