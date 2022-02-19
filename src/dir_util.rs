use std::path::Path;

pub trait SafeFileName {
    fn safe_file_name(&self) -> &str;
}

impl SafeFileName for Path {
    fn safe_file_name(&self) -> &str {
        match self.file_name() {
            Some(os_name) => match os_name.to_str() {
                Some(utf8_name) => utf8_name,
                None => {
                    panic!("Filename was not a valid utf-8 string")
                }
            },
            None => {
                panic!("Current dir does not have a file name component")
            }
        }
    }
}

pub trait AncestorsUtil {
    fn ancestors_until(&self, parent: &Self) -> Vec<&Self>;
}

impl AncestorsUtil for Path {
    fn ancestors_until(&self, parent: &Path) -> Vec<&Self> {
        let mut result: Vec<&Self> = vec![];
        let mut found_parent = false;
        for x in self.ancestors() {
            if x == parent {
                found_parent = true;
                break;
            }
            result.insert(0, x);
        }
        if !found_parent {
            panic!("Given path is not a parent of this path");
        }
        result
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::env;

    #[test]
    fn test_safe_file_name() {
        let temp_dir = env::temp_dir();
        assert!(temp_dir.is_dir());
        assert!(temp_dir.is_absolute());
        let child_dir = temp_dir.join("child");
        assert!(!child_dir.is_dir());
        assert!(child_dir.is_absolute());
        assert_eq!(child_dir.safe_file_name(), "child");
    }

    #[test]
    fn test_ancestors_until_child() {
        let temp_dir = env::temp_dir();
        let child_dir = temp_dir.join("child");
        let result = child_dir.ancestors_until(&temp_dir);
        assert_eq!(result, vec![temp_dir.join("child")]);
    }

    #[test]
    fn test_ancestors_until_grand_child() {
        let temp_dir = env::temp_dir();
        let child_dir = temp_dir.join("child").join("grand-child");
        let result = child_dir.ancestors_until(&temp_dir);
        assert_eq!(
            result,
            vec![
                temp_dir.join("child"),
                temp_dir.join("child").join("grand-child")
            ]
        );
    }
}
