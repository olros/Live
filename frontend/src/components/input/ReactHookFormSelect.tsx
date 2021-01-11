import FormControl, { FormControlProps } from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import { Controller } from 'react-hook-form';

export type IProps = FormControlProps & {
  name: string;
  label: string;
  errorMessage?: string;
  requiredMessage?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  control: any;
  defaultValue?: string;
  children: React.ReactNode;
};

const ReactHookFormSelect = ({ name, label, control, errorMessage, requiredMessage, defaultValue = '', children, ...props }: IProps) => {
  const labelId = `${name}-label`;
  return (
    <FormControl {...props}>
      <InputLabel id={labelId} required={Boolean(requiredMessage)}>
        {label}
      </InputLabel>
      <Controller
        as={
          <Select error={Boolean(errorMessage)} label={label} labelId={labelId}>
            {children}
          </Select>
        }
        control={control}
        defaultValue={defaultValue}
        name={name}
        rules={{ required: requiredMessage }}
      />
      {Boolean(errorMessage) && (
        <FormHelperText error variant='outlined'>
          {errorMessage}
        </FormHelperText>
      )}
    </FormControl>
  );
};
export default ReactHookFormSelect;
