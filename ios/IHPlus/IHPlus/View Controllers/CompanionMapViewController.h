//
//  CompanionMapViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/23/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "MapViewController.h"

@interface CompanionMapViewController : MapViewController{
    IBOutlet UIButton *_addVistaButton;
    NSArray *_actions;
}

-(IBAction)addVistaHere:(id)sender;

@end
