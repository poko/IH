//
//  MapViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/15/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface MapViewController : UIViewController <MKMapViewDelegate, UITextFieldDelegate>{
    IBOutlet MKMapView *_mapView;
    IBOutlet UITextField *_startAddress;
    IBOutlet UITextField *_endAddress;
    IBOutlet UIView *_inputHolder;
    CLGeocoder *_geocoder;
    UIActivityIndicatorView *_loadingIndicator;
    NSInteger _callCount;
    NSMutableArray *_pathPoints;
    MKPolylineView *_routeLineView;
    MKPolyline *_routeLine;
}

-(IBAction)currentLocation:(id)sender;
-(IBAction)hitTrail:(id)sender;

@end
