//
//  SearchViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SearchResultsController : UITableViewController <UISearchBarDelegate>{
    IBOutlet UILabel *_header;
    IBOutlet UITableView *_tableView;
}

@property (nonatomic, strong) NSString *searchTerm;
@property (nonatomic, strong) NSMutableArray *hikes;

-(IBAction)rewalkHike:(id)sender;

@end
