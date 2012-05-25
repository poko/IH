//
//  CompanionMapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/23/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#import "AppDelegate.h"
#import "CompanionMapViewController.h"
#import "Toast+UIView.h"
#import "MainMapViewController.h"

#define COMPANION_ALERT 5
#define LOST_HIKE_ALERT 15


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
    //NSLog(@"Companion map did load");
    [super viewDidLoad];
    _mapView = [(AppDelegate *)[[UIApplication sharedApplication] delegate] map];
}

-(void) viewWillAppear:(BOOL)animated
{
    NSLog(@"companion viewWillAppear.");
    //show dialog (if we are not currently on a companion hike)
    if (_hike == nil){
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Companion Species Mode" message:@"You have selected companion species mode. In this mode scenic vistas are chosen via a collaborative effort between you and your non-human animal companion."
                                                   delegate:self cancelButtonTitle:@"cancel" otherButtonTitles:@"continue",nil];
    [alert setTag:COMPANION_ALERT];
    [alert show];
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void) newHike: (BOOL) clearMapView{
    [super newHike:clearMapView];
    [_addVistaButton setHidden:true];
}

#pragma mark - observer
-(void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    // Do whatever you need to do here
    // update map view .. 
    Hike *curHike = [(AppDelegate *)[[UIApplication sharedApplication] delegate] hike];
    NSLog(@"companion observing hike: %@", curHike);
    if (curHike == nil ){
        NSLog(@"clearing comp. hike view, because hike is nil");
        [self newHike:true];
    }
    else if( curHike != nil && ![curHike companion]){ // this is a new "normal" hike
        // remove overlays for current hike
        NSLog(@"this should be called only if the new hike isn't a companion hike.");
        [self newHike:false];
    }
}

#pragma mark - Alert View Delegate
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    //NSLog(@"companion alert dismissed with index: %i", buttonIndex);
    [super alertView:alertView didDismissWithButtonIndex:buttonIndex]; // make sure not any of the 'common' dialogs
    if ([alertView tag] == COMPANION_ALERT){
        if (buttonIndex == 0){ //canceled
            [self.tabBarController setSelectedIndex:0];
        }
        else if (buttonIndex == 1){ //continue
            // show new alert dialog if there is a current hike
            if ([(AppDelegate *)[[UIApplication sharedApplication] delegate] hike] != nil){
                // show new alert dialog
                
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"This will erase and reset your current hike. Do you still wish to continue?"
                                                               delegate:self cancelButtonTitle:@"no" otherButtonTitles:@"yes",nil];
                [alert setTag:LOST_HIKE_ALERT];
                [alert show];
            }
        }
    }
    else if ([alertView tag] == LOST_HIKE_ALERT){ // lost hike alert
        if (buttonIndex == 0){ //canceled
            [self.tabBarController setSelectedIndex:0];
        }
        else if (buttonIndex == 1){ //continue
            // clear current hike
            [(AppDelegate *)[[UIApplication sharedApplication] delegate] setHike:nil];
        }
    }
}
- (void)uploadModalController:(UploadHikeController *)controller done:(NSString *) error
{
    [super uploadModalController:controller done:error];
    [_addVistaButton setHidden:true];
}


#pragma mark - "protected" methods

-(void) pathGenerated:(int) midpoint
{
    //NSLog(@"path generated in companion");
    // now we get some vista actions from the server to pull from
    //download vista actions
    VistaActionsDelegate *getActions = [[VistaActionsDelegate alloc] initWithHandler:^(NSArray *actions, NSString *error){
        [self hideLoadingDialog:nil];
        if (error != nil || [actions count] == 0){ // something went wrong getting .
            // use local vista actions
            _actions = [self useLocalActions:@"compantion_actions"];
        }
        else{
            _actions = actions;            
        }
        NSLog(@"Got actions: %@", _actions);
        // show the add button
        [_addVistaButton setHidden:false];
        [(AppDelegate *)[[UIApplication sharedApplication] delegate] setHike:_hike];
    }];
    NSString *url = [NSString stringWithFormat:@"%@getVistaAction.php?amount=10&companion=true", BASE_URL];
    NSLog(@"url: %@", url);
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];  
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:getActions];
    if (!connection) {
        NSLog(@"connection to get actions failed");
        [self hideLoadingDialog:@"!!Unable to connect with server"];
        // have actions locally
        _actions = [self useLocalActions:@"compantion_actions"];
        [_addVistaButton setHidden:false];
    }
    // make hike a companion hike
    [_hike setCompanion:true];
}

#pragma mark - IBActions

-(IBAction)addVistaHere:(id)sender
{ 
    if (_currentLocation == nil){
        [self.view makeToast:@"Unable to determine your current location to add vista here."];
        return;
    }
    //NSLog(@"adding new vista now. prev amt of vistas: %i", [[_hike vistas] count]);
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
    [vista setActionType:[action objectForKey:KEY_ACTION_TYPE]];//TODOx
    //[vista setActionType:@"text"];
    [vista setPrompt:[action objectForKey:KEY_ACTION_PROMPT]];
    // add vista object to hike
    [_hike addCompanionVista:vista];
    NSLog(@"added vista now. total vistas: %i", [[_hike vistas] count]);
    //show vista input
    _currentVista = vista;
    [self showActionView];
}

@end
