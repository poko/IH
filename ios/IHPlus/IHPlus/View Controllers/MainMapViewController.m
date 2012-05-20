//
//  MainMapViewController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"
#import "MainMapViewController.h"
#import "RewalkHikeDelegate.h"

#define RANDOM_INT(min, max) (min + arc4random() % ((max + 1) - min))

@implementation MainMapViewController


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
    // set map
    [(AppDelegate *)[[UIApplication sharedApplication] delegate] setMap:_mapView];
    //_monitoredRegions = [NSMutableArray array];
    NSArray *regionArray = [[_locMgr monitoredRegions] allObjects]; 
    //NSLog(@"removing all previous monitored regions? %i", [regionArray count]);
    for (int i = 0; i < [regionArray count]; i++) { 
        [_locMgr stopMonitoringForRegion:[regionArray objectAtIndex:i]];
    }
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    // remove any pending proximity alerts
    NSArray *regionArray = [[_locMgr monitoredRegions] allObjects]; 
    //NSLog(@"removing all previous monitored regions? %i", [regionArray count]);
    for (int i = 0; i < [regionArray count]; i++) {
        [_locMgr stopMonitoringForRegion:[regionArray objectAtIndex:i]];
    }
    
}

#pragma mark - geofencing
- (BOOL)registerRegionWithCircularOverlay:(CLLocationCoordinate2D)coord andIdentifier:(ScenicVista*)vista
{
    // Do not create regions if support is unavailable or disabled.
    if ( ![CLLocationManager regionMonitoringAvailable] ||
        ![CLLocationManager regionMonitoringEnabled] )
        return NO;
    
    // If the radius is too large, registration fails automatically,
    // so clamp the radius to the max value.
    CLLocationDegrees radius = 10;
    if (radius > _locMgr.maximumRegionMonitoringDistance)
        radius = _locMgr.maximumRegionMonitoringDistance;
    
    // Create the region and start monitoring it.
    CLRegion* region = [[CLRegion alloc] initCircularRegionWithCenter:coord
                                                               radius:radius identifier:[vista actionId]];
    //[_monitoredRegions addObject:region];
    [_locMgr startMonitoringForRegion:region
                      desiredAccuracy:kCLLocationAccuracyBest];
    [vista setRegion:region];
    return YES;
}

-(void) drawVistas
{
    //NSLog(@"drawing this many vistas: %i", [[_hike vistas] count]);
    for (int i = 0; i < [[_hike vistas] count]; i++){
        ScenicVista *vista = [[_hike vistas] objectAtIndex:i];
        MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
        [annotationPoint setCoordinate:[[vista location] coordinate]];
        [_mapView addAnnotation:annotationPoint];
        // enable geofence
        [self registerRegionWithCircularOverlay:[[vista location] coordinate] andIdentifier:vista];
        //NSLog(@"Vista fence at coord: %@", [vista location]);
    }
}

#pragma mark - hike observer
-(void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    // Do whatever you need to do here
    // update map view .. 
    Hike *curHike = [(AppDelegate *)[[UIApplication sharedApplication] delegate] hike];
    NSLog(@"main view observing hike. it changed! %@", curHike);
    if (curHike == nil || [curHike companion]){ // this is a new companion hike
        // remove overlays for current hike
        NSLog(@"main view clearing hike, because it's a new companion hike...");
        [self newHike:false];
    }
}

#pragma mark - "protected" methods
-(void) prepareNewHike
{
    NSLog(@"prepare new hike in Main map");
    NSArray *regionArray = [[_locMgr monitoredRegions] allObjects]; 
    //NSLog(@"removing all previous monitored regions? %i", [regionArray count]);
    for (int i = 0; i < [regionArray count]; i++) { 
        [_locMgr stopMonitoringForRegion:[regionArray objectAtIndex:i]];
    }

}

-(void) pathGenerated:(int) midpoint
{
    //NSLog(@"path generated in Main map");
    // generate random scenic vistas
    // make end point and mid point SVs
    [_hike addVista:(CLLocation *)[[_hike points] objectAtIndex:midpoint]];
    [_hike addVista:(CLLocation *)[[_hike points] objectAtIndex:([[_hike points] count] - 1)]];
    // if we don't have enough points to select randomly, make all points vistas
    if ([[_hike points] count] - 2 < 8){
        for (int i = 1; i < [[_hike points] count]; i++){
            [_hike addVista:[[_hike points] objectAtIndex:i]];
        }
    }
    else{
        // 1-3 additional vista points per half
        int vistaAmount = RANDOM_INT(1, 3);
        int randIndex;
        for (int i = 0; i < vistaAmount; i++){
            randIndex = RANDOM_INT(0, midpoint);
            [_hike addVista:(CLLocation *)[[_hike points] objectAtIndex:randIndex]];
        }
        vistaAmount = RANDOM_INT(1, 3);
        for (int i = 0; i < vistaAmount; i++){
            randIndex = RANDOM_INT(midpoint, ([[_hike points] count] - 1));
            [_hike addVista:(CLLocation *)[[_hike points] objectAtIndex:randIndex]];
        }
    }
    
    //download vista actions
    VistaActionsDelegate *getActions = [[VistaActionsDelegate alloc] initWithHandler:^(NSArray *actions, NSString *error){
        [self hideLoadingDialog:error];
        if (error != nil){ // something went wrong.
            // get actions from local plist
            actions = [self useLocalActions:@"vista_actions"];
        }
        if ([actions count] < [[_hike vistas] count]){
            NSLog(@"something went terribly wrong!");
            return;
        }
        int i = 0;
        for (ScenicVista *vista in [_hike vistas]){
            NSDictionary *action = [actions objectAtIndex:i];
            [vista setActionId:[action objectForKey:KEY_ACTION_ID]];
            [vista setActionType:[action objectForKey:KEY_ACTION_TYPE]];//TODOx 
            //[vista setActionType:@"text"];
            [vista setPrompt:[action objectForKey:KEY_ACTION_PROMPT]];
            i++;
        }
        // draw & enable geofences for vistas
        [self drawVistas];
        [(AppDelegate *)[[UIApplication sharedApplication] delegate] setHike:_hike];
    }];
    NSString *url = [NSString stringWithFormat:@"%@getVistaAction.php?amount=%i", BASE_URL, [[_hike vistas] count]];
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];  
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:getActions];
    if (!connection) {
        //NSLog(@"connection to get actions failed");
        [self hideLoadingDialog:@"Unable to connect with server"];
    }
}



#pragma mark - rewalk hike
- (void) rewalkHike:(NSString *)hikeId
{
    [self showLoadingDialog];
    // make server call
    NSString *url = [NSString stringWithFormat:@"%@getHike.php?hike_id=%@", BASE_URL, hikeId];
    //NSLog(@"Sending to url %@", url);
    RewalkHikeDelegate *rewalkDelegate = [[RewalkHikeDelegate alloc] initWithHandler:^(bool result, Hike *hike) {
        //NSLog(@"tralala back in the map view with a hike! %@", hike);
        //clear any existing paths and vistas!
        [self removeOverlaysAndAnnotations];
        [self hideLoadingDialog:nil];
        [_inputHolder setHidden:true];
        _hike = hike;
        // center map at begining of hike
        MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance([[[_hike points] objectAtIndex:0 ] coordinate], 400, 400);
        [_mapView setRegion:region animated:YES];
        // draw path and vistas
        [self drawPath];
        [self drawVistas];
    }];
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]]; 
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:rewalkDelegate];
    if (!connection) {
        //NSLog(@"connection failed");
        [self hideLoadingDialog:@"There was an error connecting to the server."];
    }
    
}

@end
