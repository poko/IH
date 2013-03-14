//
//  NoteModalController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/10/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import <UIKit/UIKit.h>

@class NoteModalController;

@protocol NoteModalControllerDelegate <NSObject>
- (void)noteModalController:(NoteModalController *)controller done:(NSString *) note;
@end

@interface NoteModalController: UIViewController<UITextViewDelegate> 

@property (nonatomic, weak) id <NoteModalControllerDelegate> vcDelegate;
@property (nonatomic, strong) IBOutlet UIView *holder;
@property (nonatomic, strong) IBOutlet UILabel *prompt;
@property (nonatomic, strong) IBOutlet UITextView *input;
@property (nonatomic, strong) IBOutlet UIBarButtonItem *doneButton;
@property (nonatomic, strong) IBOutlet UIScrollView *scrollView;
@property (nonatomic, strong) NSString *promptText;

-(IBAction)doneClick:(id)sender;

@end
