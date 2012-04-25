//
//  CompanionMapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/23/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#import "AppDelegate.h"
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
    NSLog(@"Companion map did load");
    [super viewDidLoad];
    _mapView = [(AppDelegate *)[[UIApplication sharedApplication] delegate] map];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

#pragma mark - "protected" methods
-(void) prepareNewHike
{
    NSLog(@"prepare hike in compantion");
}

-(void) pathGenerated:(int) midpoint
{
    NSLog(@"path generated in compantion");
    // now we get some vista actions from the server to pull from
    //download vista actions
    VistaActionsDelegate *getActions = [[VistaActionsDelegate alloc] initWithHandler:^(NSArray *actions, NSString *error){
        [self hideLoadingDialog:error];
        if (error != nil){ // something went wrong.
            return;
        }
        _actions = actions;
        // show the add button
        [_addVistaButton setHidden:false];
    }];
    NSString *url = @"http://localhost:8888/IHServer/getVistaAction.php?amount=10";
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];  
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:getActions];
    if (!connection) {
        NSLog(@"connection to get actions failed");
        [self hideLoadingDialog:@"Unable to connect with server"];
    }
}

#pragma mark - IBActions

-(IBAction)addVistaHere:(id)sender
{ 
    // drop pin on map at current location
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = _currentLocation.location.coordinate;
    [_mapView addAnnotation:annotationPoint];
    //next action (size of existing vistas) to get our action from.
    NSDictionary *action = [_actions objectAtIndex:[[_hike vistas] count]];
    // create vista
    ScenicVista *vista = [[ScenicVista alloc] init];
    [vista setLocation:_currentLocation.location];
    [vista setActionId:[action objectForKey:@"action_id"]];
//    [vista setActionType:[action objectForKey:@"action_type"]]; //TODO
    [vista setActionType:@"note"]; 
    [vista setPrompt:[action objectForKey:@"verbiage"]];
    // add vista object to hike
    [_hike addCompanionVista:vista];
    
    //show vista input
    _currentVista = vista;
    [self showActionView];
}

@end
