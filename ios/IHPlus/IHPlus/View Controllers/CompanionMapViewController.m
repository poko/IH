//
//  CompanionMapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/23/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#import "AppDelegate.h"
#import "CompanionMapViewController.h"
#import "Constants.h"

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
    NSString *url = [NSString stringWithFormat:@"%@getVistaAction.php?amount=10&companion=true", BASE_URL];
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];  
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:getActions];
    if (!connection) {
        NSLog(@"connection to get actions failed");
        [self hideLoadingDialog:@"Unable to connect with server"];
    }
    // make hike a companion hike
    [_hike setCompanion:true];
}

#pragma mark - IBActions

-(IBAction)addVistaHere:(id)sender
{ 
    NSLog(@"adding vista now. total vistas: %i", [[_hike vistas] count]);
    // drop pin on map at current location
    //TODO - vista image
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = _currentLocation.location.coordinate;
    NSLog(@"vista point lat: %f",   _currentLocation.location.coordinate.latitude );
    NSLog(@"vista point lng: %f",   _currentLocation.location.coordinate.longitude );
    [_mapView addAnnotation:annotationPoint];
    //next action (size of existing vistas) to get our action from.
    NSDictionary *action = [_actions objectAtIndex:[[_hike vistas] count]];
    // create vista
    ScenicVista *vista = [[ScenicVista alloc] init];
    [vista setLocation:_currentLocation.location];
    [vista setActionId:[action objectForKey:KEY_ACTION_ID]];
//    [vista setActionType:[action objectForKey:KEY_ACTION_TYPE]]; //TODOx    
    [vista setActionType:@"text"]; 
    [vista setPrompt:[action objectForKey:KEY_ACTION_PROMPT]];
    // add vista object to hike
    [_hike addCompanionVista:vista];
    //show vista input
    _currentVista = vista;
    [self showActionView];
}

@end
