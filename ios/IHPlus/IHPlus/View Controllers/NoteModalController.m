//
//  NoteModalController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/10/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "NoteModalController.h"
#import <QuartzCore/QuartzCore.h>

@implementation NoteModalController

@synthesize vcDelegate, promptText;
@synthesize holder, prompt, input, doneButton, scrollView;

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
    [prompt setText:promptText];
    //allows user to hide keyboard.
    UITapGestureRecognizer *singleFingerTap = [[UITapGestureRecognizer alloc] initWithTarget:self 
                                            action:@selector(resignResponder:)];
    [holder addGestureRecognizer:singleFingerTap];
    [self registerForKeyboardNotifications];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    self.vcDelegate = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void)resignResponder:(id)sender
{
    [input resignFirstResponder];
}

-(IBAction)doneClick:(id)sender
{
    [vcDelegate noteModalController:self done:[input text]];
}

#pragma mark textView delegate
- (void)textViewDidChange:(UITextView *)textView
{
    [doneButton setEnabled:[[textView text] length] > 0];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    
    return YES;
}

#pragma mark keyboard handling

- (void)registerForKeyboardNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillBeHidden:)
                                                 name:UIKeyboardWillHideNotification object:nil];
    
}

- (void)keyboardWasShown:(NSNotification*)notification {
    // move text field
    NSDictionary* info = [notification userInfo];
    CGSize keyboardSize = [[info objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue].size;
    UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0, 0.0, keyboardSize.height, 0.0);
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
    
    // If active text field is hidden by keyboard, scroll it so it's visible
    // Your application might not need or want this behavior.
    //CGRect aRect = self.view.frame;
    //aRect.size.height -= keyboardSize.height;
    //if (!CGRectContainsPoint(aRect, input.frame.origin) ) {
        CGPoint scrollPoint = CGPointMake(0.0, abs(input.frame.origin.y+input.frame.size.height-keyboardSize.height));
        [scrollView setContentOffset:scrollPoint animated:YES];
   // }
}

- (void)keyboardWillBeHidden:(NSNotification*)notification {
    // move text field back
    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    scrollView.contentInset = contentInsets;
    scrollView.scrollIndicatorInsets = contentInsets;
}

@end
