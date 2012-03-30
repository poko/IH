//
//  TestMapVC.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/27/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface SearchViewController : UIViewController{
    IBOutlet UISearchBar *_searchBar;
    CLGeocoder *_geocoder;
    UIActivityIndicatorView *_loadingIndicator;
    NSMutableArray *_hikes;
}

@end
