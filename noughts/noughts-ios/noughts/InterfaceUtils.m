#import "InterfaceUtils.h"

@implementation InterfaceUtils
+ (UIStoryboard*)mainStoryboard {
  NSString *storyboardName = UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad ?
      @"Main_iPad" : @"Main_iPhone";
  return [UIStoryboard storyboardWithName:storyboardName bundle: nil];
}

+ (void)error:(NSString*)message {
  NSLog(@"%@", message);
  @throw message;
}
@end
