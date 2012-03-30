//
//  SearchViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SearchResultsController : UITableViewController <UISearchBarDelegate>{
    IBOutlet UITableView *_tableView;
}

@property (nonatomic, strong) NSMutableArray *hikes;

@end
