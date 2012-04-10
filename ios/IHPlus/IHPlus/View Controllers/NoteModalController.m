//
//  NoteModalController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/10/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NoteModalController.h"
#import <QuartzCore/QuartzCore.h>

@implementation NoteModalController

@synthesize vcDelegate;
@synthesize prompt, input, doneButton;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    [doneButton setEnabled:NO];
    // make input look input-y
    [input.layer setBackgroundColor: [[UIColor whiteColor] CGColor]];
    [input.layer setBorderColor: [[UIColor grayColor] CGColor]];
    [input.layer setBorderWidth: 1.0];
    [input.layer setCornerRadius: 8.0f];
    [input.layer setMasksToBounds:YES];
    [input setDelegate:self];
    
    //TODO figure this out better self.view includes nav controller bar
//    UITapGestureRecognizer *singleFingerTap = [[UITapGestureRecognizer alloc] initWithTarget:self 
//                                            action:@selector(resignResponder:)];
//    [self.view addGestureRecognizer:singleFingerTap];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void)resignResponder:(id)sender
{
    NSLog(@"resigning!");
    [input resignFirstResponder];
}

-(IBAction)doneClick:(id)sender
{
    NSLog(@"done clicked");
    [vcDelegate noteModalController:self done:[input text]];
}

#pragma mark textView delegate
- (void)textViewDidChange:(UITextView *)textView
{
    NSLog(@"editing!");
    [doneButton setEnabled:[[textView text] length] > 0];
}

@end
