//
//  TextModalController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>
#import "NoteModalController.h"

@interface TextModalController : NoteModalController<MFMessageComposeViewControllerDelegate>

@property (nonatomic, strong) IBOutlet UIButton *textButton;
@property (nonatomic, strong) IBOutlet UILabel *disclaimer;

-(IBAction)textClick:(id)sender;


@end
