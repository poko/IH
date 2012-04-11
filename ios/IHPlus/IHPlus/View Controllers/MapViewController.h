//
//  MapViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import "Hike.h"
#import "NoteModalController.h"
#import "TextModalController.h"

@interface MapViewController : UIViewController <MKMapViewDelegate, UITextFieldDelegate, CLLocationManagerDelegate, NoteModalControllerDelegate>{
    IBOutlet MKMapView *_mapView;
    IBOutlet UITextField *_startAddress;
    IBOutlet UITextField *_endAddress;
    IBOutlet UIView *_inputHolder;
    IBOutlet UIBarButtonItem *_uploadButton;
    IBOutlet UIView *_promptHolder;
    IBOutlet UILabel *_prompt;
    CLGeocoder *_geocoder;
    UIActivityIndicatorView *_loadingIndicator;
    NSInteger _callCount;
    NSMutableArray *_pathPoints;
    MKPolylineView *_routeLineView;
    MKPolyline *_routeLine;
    Hike *_hike;
    ScenicVista *_currentVista;
    CLLocationManager *_locMgr;
    NSMutableArray *_monitoredRegions;
}

-(IBAction)currentLocation:(id)sender;
-(IBAction)hitTrail:(id)sender;
-(IBAction)continueClicked:(id)sender;

@end
