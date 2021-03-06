#import "MainMenuViewController.h"
#import "FacebookUtils.h"
#import "Identifiers.h"
#import "PushNotificationHandler.h"

@interface MainMenuViewController ()
@property(weak,nonatomic) IBOutlet UIButton *savedGamesButton;
@property(strong,nonatomic) PushNotificationHandler* pushHandler;
@end

@implementation MainMenuViewController

- (void)awakeFromNib {
  UIImage *logo = [UIImage imageNamed:@"logo_title_bar"];
  self.navigationItem.titleView = [[UIImageView alloc] initWithImage:logo];
  _pushHandler = [PushNotificationHandler new];
}

- (void)viewWillAppear:(BOOL)animated {
  if ([FacebookUtils isFacebookUser]) {
    [self removeLoginLink: NO];
  }
  [_pushHandler registerHandler];
}

- (void)viewWillDisappear:(BOOL)animated {
  [_pushHandler unregisterHandler];
}

// Hides the facebook login link
- (void)removeLoginLink:(BOOL)animated {
  if (animated) {
    [UIView animateWithDuration:0.3 animations:^{
        [self.view viewWithTag:5].alpha = 0.0;
        [self.view viewWithTag:6].alpha = 0.0;
    } completion:^(BOOL finished) {
        [[self.view viewWithTag:5] removeFromSuperview];
        [[self.view viewWithTag:6] removeFromSuperview];
    }];
  } else {
    [[self.view viewWithTag:5] removeFromSuperview];
    [[self.view viewWithTag:6] removeFromSuperview];
  }
}

- (IBAction)onFacebookLoginClicked {
  [FacebookUtils logInToFacebookWithCallback:^{
      [self removeLoginLink: YES];
  }];
}

@end
