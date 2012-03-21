//
//  SearchViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SearchViewController : UITableViewController <UISearchBarDelegate>{
    NSMutableArray *_hikes;
    IBOutlet UISearchBar *_searchBar;
    CLGeocoder *_geocoder;
}

@end
