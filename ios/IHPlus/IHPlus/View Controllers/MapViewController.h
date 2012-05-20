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
#import "UploadHikeController.h"
#import "VistaActionsDelegate.h"
#import "Constants.h"

@interface MapViewController : UIViewController <MKMapViewDelegate, UITextFieldDelegate, CLLocationManagerDelegate, NoteModalControllerDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UploadHikeControllerDelegate, UIAlertViewDelegate>{
    IBOutlet MKMapView *_mapView;
    IBOutlet UITextField *_startAddress;
    IBOutlet UITextField *_endAddress;
    IBOutlet UIView *_inputHolder;
    IBOutlet UIButton *_infoButton;
    IBOutlet UIBarButtonItem *_uploadButton;
    IBOutlet UIView *_promptHolder;
    IBOutlet UILabel *_prompt;
    CLGeocoder *_geocoder;
    UIActivityIndicatorView *_loadingIndicator;
    NSInteger _callCount;
    MKPolylineView *_routeLineView;
    MKPolyline *_routeLine;
    Hike *_hike;
    ScenicVista *_currentVista;
    CLLocationManager *_locMgr;
    MKUserLocation *_currentLocation;
    NSMutableArray *_pathPoints;
    bool _zoomed;
}

-(IBAction)currentLocation:(id)sender;
-(IBAction)hitTrail:(id)sender;
-(IBAction)continueClicked:(id)sender;
-(IBAction)uploadHike:(id)sender;
-(void)showActionView;
-(void)showLoadingDialog;
-(void)hideLoadingDialog:(NSString *) msg;
-(void)prepareNewHike;
-(void)pathGenerated:(int) midpoint;
-(void)removeOverlaysAndAnnotations;
-(void)drawPath;
-(NSArray *) useLocalActions:(NSString *) plist;

@end
