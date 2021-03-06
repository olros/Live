import React from 'react';

// Material UI Components
import { makeStyles, Theme } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import MaterialDialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';

const useStyles = makeStyles((theme: Theme) => ({
  contentText: {
    color: theme.palette.text.secondary,
  },
}));

export type DialogProps = {
  open: boolean;
  onClose: (...args: never[]) => void;
  onCancel?: (...args: never[]) => void;
  onConfirm?: (...args: never[]) => void;
  titleText?: string;
  children?: React.ReactNode;
  contentText?: string;
  closeText?: string;
  confirmText?: string;
};

function Dialog({ open, onClose, onCancel, onConfirm, titleText, children, contentText, closeText, confirmText }: DialogProps) {
  const classes = useStyles();
  return (
    <MaterialDialog aria-labelledby='form-dialog-title' onClose={onClose} open={open}>
      {titleText && <DialogTitle id='form-dialog-title'>{titleText}</DialogTitle>}
      {(contentText || children) && (
        <DialogContent>
          {contentText && <DialogContentText className={classes.contentText}>{contentText}</DialogContentText>}
          {children}
        </DialogContent>
      )}
      <DialogActions>
        <Button color='primary' onClick={onCancel || onClose}>
          {closeText || 'Close'}
        </Button>
        {onConfirm && (
          <Button color='primary' onClick={onConfirm} variant='contained'>
            {confirmText || 'OK'}
          </Button>
        )}
      </DialogActions>
    </MaterialDialog>
  );
}

export default Dialog;
