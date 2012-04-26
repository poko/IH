//
//  UploadHikeController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/13/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UploadHikeController.h"
#import "UploadHikeDelegate.h"

@implementation UploadHikeController

@synthesize vcDelegate, hike;
@synthesize hikeName, hikeDesc, userName, scroller, uploadButton;


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

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    [hikeName setDelegate:self];
    [hikeDesc setDelegate:self];
    [userName setDelegate:self];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                             selector:@selector(validateFields:) 
                                                 name:UITextFieldTextDidChangeNotification 
                                               object:hikeName];
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                             selector:@selector(validateFields:) 
                                                 name:UITextFieldTextDidChangeNotification 
                                               object:hikeDesc];
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                             selector:@selector(validateFields:) 
                                                 name:UITextFieldTextDidChangeNotification 
                                               object:userName];
    
    //allows user to hide keyboard.
    UITapGestureRecognizer *singleFingerTap = [[UITapGestureRecognizer alloc] 
                                               initWithTarget:self 
                                               action:@selector(dismissKeyboard:)];
    [scroller addGestureRecognizer:singleFingerTap];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - IBActions

-(IBAction)uploadClick:(id)sender
{
    // set hike values
    [hike setName:[hikeName text]];
    [hike setDescription:[hikeDesc text]];
    [hike setUsername:[userName text]];
    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"http://localhost:8888/IHServer/createHike.php"]];
    NSLog(@"url: %@", req.URL);
    // set POST parameters
    [req setHTTPMethod:@"POST"];
    NSLog(@"trying to log upload data: %@", [hike getUploadData]);
    [req setHTTPBody:[hike getUploadData]];
    UploadHikeDelegate *connDelegate = [[UploadHikeDelegate alloc] initWithHandler:^(bool success, NSString *error) {
    NSLog(@"upload handler gets: %i", success);
    if (success){
        NSLog(@"success! delegate? %@", vcDelegate);
        //TODO - dismiss loading dialog and modal
        [vcDelegate uploadModalController:self done:nil];
        return;
    }
    else{
        NSLog(@"error! %@", error);
        //TODO - display error, allow user to re-try uploading
    }
    }];
    
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:connDelegate];
    if (!connection) {
        NSLog(@"connection failed");
        //TODO
        //[self hideLoadingDialog:@"Could not connect to server"];
    }
}

#pragma mark TextField Delegate
CGSize keyboardSize;
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == hikeName || textField == hikeDesc){
        NSLog(@"name next");
        NSInteger nextTag = textField.tag + 1;
        // Try to find next responder
        UIResponder* nextResponder = [textField.superview viewWithTag:nextTag];
        if (nextResponder) {
            // Found next responder, so set it.
            [nextResponder becomeFirstResponder];
            // Step 3: Scroll the target text field into view.
            CGRect aRect = self.view.frame;
            aRect.size.height -= (keyboardSize.height + 44);
            if (!CGRectContainsPoint(aRect, activeTextField.frame.origin) ) {
                CGPoint scrollPoint = CGPointMake(0.0, activeTextField.frame.origin.y - (keyboardSize.height-15-44));
                [scroller setContentOffset:scrollPoint animated:YES];
            }
        } else {
            // Not found, so remove keyboard.
            [textField resignFirstResponder];
        }
    }
    else if (textField == userName){
        NSLog(@"user done");
        [textField resignFirstResponder];
    }
    return NO;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    activeTextField = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    activeTextField = nil;
}

- (void)validateFields:(NSNotification *)notification
{
    NSLog(@"validating");
    [uploadButton setEnabled:([[userName text] length] > 0 
                              && [[hikeDesc text] length] > 0 
                              && [[hikeName text] length] > 0)];
}

# pragma mark - keyboard handling
- (void)keyboardWasShown:(NSNotification *)notification
{
    
    // Step 1: Get the size of the keyboard.
    keyboardSize = [[[notification userInfo] objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue].size;
    
    NSLog(@"keyboard shown: %f", keyboardSize.height);
    // Step 2: Adjust the bottom content inset of your scroll view by the keyboard height.
    UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0, 0.0, keyboardSize.height, 0.0);
    scroller.contentInset = contentInsets;
    scroller.scrollIndicatorInsets = contentInsets;
    
    // Step 3: Scroll the target text field into view.
    CGRect aRect = self.view.frame;
    aRect.size.height -= (keyboardSize.height + 44);
    if (!CGRectContainsPoint(aRect, activeTextField.frame.origin) ) {
        CGPoint scrollPoint = CGPointMake(0.0, activeTextField.frame.origin.y - (keyboardSize.height-15-44));
        [scroller setContentOffset:scrollPoint animated:YES];
    }
}

- (void) keyboardWillHide:(NSNotification *)notification {
    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    scroller.contentInset = contentInsets;
    scroller.scrollIndicatorInsets = contentInsets;
}

- (IBAction)dismissKeyboard:(id)sender
{
    [activeTextField resignFirstResponder];
}

@end
